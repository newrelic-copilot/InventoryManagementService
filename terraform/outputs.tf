output "security_group_id" {
  description = "ID of the InventoryManagementService security group."
  value       = aws_security_group.inventory_management_service.id
}

output "security_group_arn" {
  description = "ARN of the InventoryManagementService security group."
  value       = aws_security_group.inventory_management_service.arn
}
