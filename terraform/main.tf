# 1. Resource Group
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
  tags     = var.tags
}

# 2. Rețeaua virtuală și Subnet-ul
resource "azurerm_virtual_network" "vnet" {
  name                = "healthconnect-vnet"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  address_space       = var.vnet_address_space
  tags                = var.tags
}

resource "azurerm_subnet" "subnet" {
  name                 = "healthconnect-subnet"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.1.0/24"]

  depends_on = [azurerm_virtual_network.vnet]
}

# 3. Registrul Docker (ACR)
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
  tags                = var.tags
}

# 4. Azure SQL Server & Baza de Date
resource "azurerm_mssql_server" "sql_server" {
  name                         = "healthconnect-sql-server-2026"
  resource_group_name          = azurerm_resource_group.rg.name
  location                     = azurerm_resource_group.rg.location
  version                      = "12.0"
  administrator_login          = var.sql_admin_user
  administrator_login_password = var.sql_admin_password
  tags                         = var.tags
}

resource "azurerm_mssql_database" "sql_db" {
  name                        = var.sql_db_name
  server_id                   = azurerm_mssql_server.sql_server.id
  sku_name                    = var.sql_sku_name
  storage_account_type        = "Local" # <--- ADAUGĂ ACEASTĂ LINIE
  auto_pause_delay_in_minutes = 60
  tags                        = var.tags
  min_capacity                = 0.5
}

# Permite accesul serviciilor Azure (Spring Boot din AKS) la SQL
resource "azurerm_mssql_firewall_rule" "allow_azure_services" {
  name             = "AllowAzureServices"
  server_id        = azurerm_mssql_server.sql_server.id
start_ip_address = "79.118.136.121"
  end_ip_address   = "79.118.136.121"
}

resource "azurerm_kubernetes_cluster" "aks" {
  name                = var.aks_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  dns_prefix          = var.aks_dns_prefix

  default_node_pool {
    name       = "default"
    node_count = 1
    vm_size    = "Standard_B2s_v2" # <--- Schimbat la versiunea v2
  }

  identity {
    type = "SystemAssigned"
  }

  tags = var.tags
}

# 6. Permisiunea ca AKS să descarce imagini din ACR
resource "azurerm_role_assignment" "aks_acr_pull" {
  principal_id                     = azurerm_kubernetes_cluster.aks.kubelet_identity[0].object_id
  role_definition_name             = "AcrPull"
  scope                            = azurerm_container_registry.acr.id
  skip_service_principal_aad_check = true
}