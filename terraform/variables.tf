#---Configurare generala---

variable "location" {
  type = string
  default = "spaincentral"
  description = "Regiunea unde vor fi create resursele in Azure"
}

variable "resource_group_name" {
    type = string
    default = "rg-healthconnect"
    description = "Numelere resursei de grup in azure"
}

variable "tags" {
    type = map(string)
    default = {
        Environment = "Development"
        Project     = "HealthConnect"
        ManagedBy   = "Terraform"
    }
    description = "Tag urile aplicate resurselor din azure"
}

#---Retea---

variable "vnet_address_space" {
  type        = list(string)
  default     = ["10.0.0.0/16"]
  description = "Clasa de adrese IP pentru rețeaua virtuală"
}

variable "subnet_address_prefix" {
  type        = list(string)
  default     = ["10.0.1.0/24"]
  description = "Clasa de adrese IP pentru subnet-ul AKS"
}

#---ACR---
variable "acr_name" {
  type        = string
  default     = "healthconnectacr2026"
  description = "Numele unic global pentru Azure Container Registry (doar litere mici și cifre)"
}

#---SQL---

variable "sql_admin_user" {
  type        = string
  default     = "adrianfl24"
  description = "Utilizatorul administrator pentru Azure SQL"
}

variable "sql_admin_password" {
  type        = string
  default     = "rapid1923bU1"
  sensitive   = true
  description = "Parola administratorului pentru Azure SQL"
}

variable "sql_db_name" {
  type        = string
  default     = "healthconnect-db"
  description = "Numele bazei de date SQL"
}

variable "sql_sku_name" {
  type        = string
  default     = "GP_S_Gen5_1"
  description = "Planul de tarifare și performanță pentru baza de date (Serverless)"
}

#---aks---

variable "aks_cluster_name" {
  type        = string
  default     = "healthconnect-aks"
  description = "Numele clusterului Azure Kubernetes Service"
}

variable "aks_vm_size" {
  type        = string
  default     = "Standard_B2s"
  description = "Tipul și mărimea mașinilor virtuale din clusterul AKS"
}

variable "aks_node_count" {
  type        = number
  default     = 2
  description = "Numărul de noduri (servere) din cluster"
}

variable "aks_dns_prefix" {
  type        = string
  description = "Prefixul DNS pentru clusterul AKS"
  default     = "healthconnect-aks"
}

variable "aks_name" {
  type        = string
  description = "Numele clusterului AKS"
  default     = "healthconnect-aks"
}