terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Security group for the InventoryManagementService EC2 instances.
# Affected resource: arn:aws:ec2:us-east-2:222634381402:security-group/sg-064d2f895824a22f1
#
# High-risk ports (SSH/22, RDP/3389) are restricted to trusted CIDR ranges.
# No ingress rule allows 0.0.0.0/0 or ::/0 on any sensitive management port.
resource "aws_security_group" "inventory_management_service" {
  name        = "inventory-management-service"
  description = "Security group for InventoryManagementService EC2 instances. High-risk management ports are restricted to approved CIDR ranges."
  vpc_id      = var.vpc_id

  # SSH access restricted to the corporate VPN / bastion host CIDR range only.
  # Owner: platform-infra | Approved source: corporate VPN
  ingress {
    description = "SSH from trusted admin CIDR (corporate VPN)"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.trusted_admin_cidrs
  }

  # RDP access restricted to the jump host security group only.
  # Owner: platform-infra | Approved source: jump host security group
  ingress {
    description     = "RDP from jump host security group only"
    from_port       = 3389
    to_port         = 3389
    protocol        = "tcp"
    security_groups = [var.jump_host_security_group_id]
  }

  # Application HTTP traffic — allowed from the load balancer security group only.
  # Owner: platform-infra | Approved source: ALB security group
  ingress {
    description     = "HTTP from ALB security group"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [var.alb_security_group_id]
  }

  # Allow all outbound traffic.
  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "inventory-management-service"
    Environment = var.environment
    Owner       = "platform-infra"
    ManagedBy   = "terraform"
    Purpose     = "InventoryManagementService EC2 instance security group"
    Compliance  = "NIST-800-53,NIST-800-171"
  }
}
