variable "vpc_id" {
  description = "The ID of the VPC where the security group will be created"
  type        = string
}

variable "admin_cidr_blocks" {
  description = "List of approved CIDR ranges for SSH (port 22) administrative access. Must not include 0.0.0.0/0 or ::/0."
  type        = list(string)

  validation {
    condition     = !contains(var.admin_cidr_blocks, "0.0.0.0/0") && !contains(var.admin_cidr_blocks, "::/0")
    error_message = "admin_cidr_blocks must not allow unrestricted access (0.0.0.0/0 or ::/0) on high-risk port 22."
  }
}

variable "management_cidr_blocks" {
  description = "List of approved CIDR ranges for RDP (port 3389) management access. Must not include 0.0.0.0/0 or ::/0."
  type        = list(string)

  validation {
    condition     = !contains(var.management_cidr_blocks, "0.0.0.0/0") && !contains(var.management_cidr_blocks, "::/0")
    error_message = "management_cidr_blocks must not allow unrestricted access (0.0.0.0/0 or ::/0) on high-risk port 3389."
  }
}

variable "environment" {
  description = "Deployment environment (e.g. production, staging)"
  type        = string
  default     = "production"
}
