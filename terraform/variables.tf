# Required input variables for the InventoryManagementService Terraform configuration.

variable "aws_region" {
  description = "AWS region where the security group will be managed (e.g. us-east-2)."
  type        = string
  default     = "us-east-2"
}

variable "vpc_id" {
  description = "The ID of the VPC in which the security group will be created."
  type        = string
}

variable "environment" {
  description = "Deployment environment name (e.g. production, staging)."
  type        = string
}

# Trusted CIDR ranges for SSH access (port 22).
# Must be scoped to corporate VPN or bastion host subnets — never 0.0.0.0/0.
variable "trusted_admin_cidrs" {
  description = "List of trusted CIDR ranges allowed to reach SSH (port 22). Must NOT include 0.0.0.0/0 or ::/0."
  type        = list(string)

  validation {
    condition = alltrue([
      for cidr in var.trusted_admin_cidrs :
      cidr != "0.0.0.0/0" && cidr != "::/0"
    ])
    error_message = "trusted_admin_cidrs must not contain 0.0.0.0/0 or ::/0. Restrict SSH to trusted admin CIDR ranges only."
  }
}

# Security group ID of the jump host from which RDP (port 3389) is permitted.
variable "jump_host_security_group_id" {
  description = "Security group ID of the approved jump host. RDP (port 3389) is restricted to this source only."
  type        = string
}

# Security group ID of the Application Load Balancer that fronts the service.
variable "alb_security_group_id" {
  description = "Security group ID of the ALB. Application traffic (port 8080) is restricted to this source only."
  type        = string
}
