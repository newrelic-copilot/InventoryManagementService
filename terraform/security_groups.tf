# Security group for InventoryManagementService instances.
# ARN: arn:aws:ec2:us-east-2:222634381402:security-group/sg-0da688dac8d2b15d7
#
# High-risk ports (SSH/22 and RDP/3389) are restricted to trusted admin CIDRs
# only. Do NOT expand these rules to 0.0.0.0/0 or ::/0.
resource "aws_security_group" "inventory_management_service" {
  name        = "inventory-management-service"
  description = "Security group for InventoryManagementService - restricts high-risk port access to trusted networks"
  vpc_id      = var.vpc_id

  # SSH access restricted to trusted admin networks only.
  # Owner: platform-ops | Purpose: administrative access | Ref: security-issue-sg-0da688dac8d2b15d7
  ingress {
    description = "SSH from trusted admin network"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = var.trusted_admin_cidrs
  }

  # RDP access restricted to trusted admin networks only.
  # Owner: platform-ops | Purpose: administrative access | Ref: security-issue-sg-0da688dac8d2b15d7
  ingress {
    description = "RDP from trusted admin network"
    from_port   = 3389
    to_port     = 3389
    protocol    = "tcp"
    cidr_blocks = var.trusted_admin_cidrs
  }

  egress {
    description      = "Allow all outbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name        = "inventory-management-service"
    Environment = "production"
    ManagedBy   = "terraform"
    Owner       = "platform-ops"
    SecurityRef = "sg-0da688dac8d2b15d7"
  }
}
