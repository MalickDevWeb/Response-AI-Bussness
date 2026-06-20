variable "aws_region" {
  description = "Région AWS pour le déploiement"
  type        = string
  default     = "us-east-1"
}

variable "public_key" {
  description = "Clé publique SSH (ex: contenu de ~/.ssh/id_rsa.pub) pour se connecter à l'instance"
  type        = string
}
