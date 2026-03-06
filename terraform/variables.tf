variable "vpc_id" {
  description = "The VPC ID in which the security group resides"
  type        = string
}

variable "trusted_admin_cidrs" {
  description = "List of trusted CIDR ranges allowed to access high-risk administrative ports (SSH/22, RDP/3389). Replace with your approved admin network ranges."
  type        = list(string)
  # Example: ["10.0.0.0/8", "192.168.1.0/24"]
  # Do NOT use 0.0.0.0/0 or ::/0 here.

  validation {
    condition     = !contains(var.trusted_admin_cidrs, "0.0.0.0/0") && !contains(var.trusted_admin_cidrs, "::/0")
    error_message = "trusted_admin_cidrs must not contain 0.0.0.0/0 or ::/0. Restrict access to specific trusted admin networks only."
  }
}
