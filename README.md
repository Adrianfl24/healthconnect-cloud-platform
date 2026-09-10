HealthConnect — Cloud Platform

A backend API for a healthcare management system. Built with Spring Boot and deployed on
Microsoft Azure using Terraform, Docker, and Kubernetes (AKS). The release process is fully
automated via a CI/CD pipeline in GitHub Actions.
This project focuses on infrastructure provisioning, containerization, and cloud deployment
best practices rather than complex business logic.

Architecture & Traffic Flow

1. Source Code: Java 17 REST API built with Spring Boot, Spring Data JPA, and Hiber-
nate.
2. Database: Azure SQL Database accessed securely via TLS.
3. Infrastructure as Code: Terraform scripts managing the Azure Resource Group, Virtual
Network, Subnets, Azure Container Registry (ACR), Azure SQL Server, and AKS Cluster.
4. CI/CD: GitHub Actions workflow that compiles the app, builds the Docker image, pushes
it to ACR, and updates the deployment on AKS.
5. Ingress: Azure Load Balancer exposing the Kubernetes service to the internet.
[ Developer Push (PR) ]
          |
          v
[ GitHub Actions CI/CD ] --(Build & Push)--> [ Azure Container Registry ]
          ^
          | (AcrPull via Managed Identity)
          v
[ Internet Users ] --> [ Azure Load Balancer ] --> [ Azure Kubernetes Service ]
          |
          v (Port 1433 / TLS)
[ Azure SQL Database ]

Tech Stack

• Backend: Java 17, Spring Boot 3, Maven
• Database: Azure SQL, HikariCP
• Cloud: Microsoft Azure
• IaC & Tooling: Terraform, Docker, Kubernetes (kubectl), Azure CLI
• CI/CD: GitHub Actions

Engineering Decisions & Troubleshooting

• Passwordless Registry Access: Instead of hardcoding registry credentials into Kuber-
netes secrets, I attached ACR directly to AKS (az aks update –attach-acr) using Azure
Managed Identity (AcrPull role). This eliminates secret rotation overhead.
• Secrets Management: Managed sensitive credentials (ACR passwords, Kubeconfig) se-
curely via GitHub Actions Secrets, ensuring no hardcoded access tokens or credentials
exist in the source code repository.
• Database Connectivity & Networking: Resolved HikariCP TLS handshake timeouts
by adjusting JDBC parameters (encrypt=true, trustServerCertificate=false). For
this demo environment, Azure internal traffic is permitted via a 0.0.0.0 firewall rule, while
the production architecture dictates using Azure Private Endpoints for VNet isolation.
• Optimized Container Builds: Used a multi-stage Dockerfile. Stage 1 compiles the
.jar using a Maven image, and Stage 2 runs it on a minimal JRE image (Eclipse Temurin)
to reduce the final image size by ∼65% and limit the attack surface.
• Zero-Downtime Rollouts: Deployment manifests configure rolling updates so pods are
replaced gradually without dropping active HTTP traffic.

Project Structure
.
.github/workflows/
pr-deploy.yml # CI/CD pipeline definitions
k8s/
configmap.yaml # App environment variables (JDBC URL, profiles)
secret.template.yaml # DB credentials template (real values excluded via .gitignore)
deployment.yaml # Pod spec, resource limits, replicas
service.yaml # External LoadBalancer routing
src/ # Java Source Code
terraform/ # Terraform modules (VNet, AKS, ACR, SQL)
Dockerfile # Multi-stage build instructions
pom.xml # Maven dependencies

Local Setup & Deployment

Prerequisites
• Azure CLI (az) installed and authenticated
• Terraform CLI installed
• kubectl installed

1. Provision Infrastructure

cd terraform
terraform init
terraform plan
terraform apply -auto-approve

2. Configure Cluster Access & Networking

# Get AKS credentials
az aks get-credentials --resource-group rg-healthconnect --name healthconnect-aks
--overwrite-existing

# Attach ACR to AKS
az aks update --resource-group rg-healthconnect --name healthconnect-aks
--attach-acr healthconnectacr2026

# Open SQL firewall for internal Azure traffic (Demo simplification)
az sql server firewall-rule create \
--resource-group rg-healthconnect \
--server healthconnect-sql-server-2026 \
--name AllowAllWindowsAzureIps \
--start-ip-address 0.0.0.0 \
--end-ip-address 0.0.0.0

3. Build & Deploy
# Build image directly in Azure ACR (bypasses local Docker daemon)
az acr build --registry healthconnectacr2026 --image healthconnect-platform:latest .

# Apply Kubernetes manifests
kubectl apply -f k8s/

# Monitor rollout and get public IP
kubectl get pods -w
kubectl get svc healthconnect-service

CI/CD Workflow
The .github/workflows/pr-deploy.yml triggers on any Pull Request merged into main:
1. Build & Test: Executes mvn clean package.
2. Registry Authentication: Authenticates to Azure ACR securely using standard GitHub
Secrets (ACR_USERNAME, ACR_PASSWORD).
3. Docker Build & Push: Builds the image, tags it uniquely with the Git Commit SHA,
and pushes it to ACR.
4. Deploy: Connects to AKS using the KUBECONFIG secret and updates the cluster dynami-
cally using kubectl set image deployment/healthconnect-app healthconnect-app=healthconnectacr2026
.azurecr.io/healthconnect-platform:${{ github.sha }}

Future Roadmap
• Network Isolation: Implement Azure Private Link / Private Endpoints to completely
isolate Azure SQL traffic from the public internet, replacing the 0.0.0.0 firewall rule.
• Security: Migrate database credentials from Kubernetes Secrets to Azure Key Vault (via
CSI driver).
• Passwordless CI/CD: Migrate GitHub Actions authentication from static secrets to OpenID 
Connect (OIDC / Federated Credentials).
