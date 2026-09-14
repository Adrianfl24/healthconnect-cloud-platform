output "sql_server_fqdn" {
  value       = azurerm_mssql_server.sql_server.fully_qualified_domain_name
  description = "Adresa serverului SQL necesară în Spring Boot"
}

output "acr_login_server" {
  value       = azurerm_container_registry.acr.login_server
  description = "Serverul de login pentru Docker Registry"
}

output "aks_cluster_name" {
  value       = azurerm_kubernetes_cluster.aks.name
  description = "Numele clusterului Kubernetes"
}