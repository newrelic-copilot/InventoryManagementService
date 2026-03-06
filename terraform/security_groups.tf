resource "aws_security_group" "inventory_management_sg" {
  name                   = "inventory-management-sg"
  description            = "Security group for InventoryManagementService with restricted access to high-risk ports"
  vpc_id                 = var.vpc_id
  revoke_rules_on_delete = true

  # Restricted SSH access from approved admin network only
  ingress {
    description = "SSH from admin network"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.admin_cidr_blocks
  }

  # Restricted RDP access from management network only
  ingress {
    description = "RDP from management network"
    from_port   = 3389
    to_port     = 3389
    protocol    = "tcp"
    cidr_blocks = var.management_cidr_blocks
  }

  # Allow all outbound traffic (IPv4 and IPv6)
  egress {
    description      = "Allow all outbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name        = "inventory-management-sg"
    Environment = var.environment
    ManagedBy   = "terraform"
    Owner       = "inventory-management-team"
  }
}
