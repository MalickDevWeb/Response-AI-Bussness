provider "aws" {
  region = var.aws_region
}

# 1. Security Group pour autoriser HTTP (API), SSH et Evolution API
resource "aws_security_group" "autocloser_sg" {
  name        = "autocloser_sg"
  description = "Autoriser le trafic pour AutoCloser AI"

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Spring Boot API"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Evolution API (WhatsApp)"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 2. Key Pair (Assurez-vous de générer cette clé ou d'utiliser une existante)
resource "aws_key_pair" "autocloser_key" {
  key_name   = "autocloser-deploy-key"
  public_key = var.public_key
}

# 3. Instance EC2 (Ubuntu 24.04 ou 22.04 LTS)
resource "aws_instance" "autocloser_server" {
  ami           = "ami-04a81a99f5ec58529" # Ubuntu 24.04 LTS (us-east-1), à adapter selon la région
  instance_type = "t3.small" # t3.small recommandé pour Spring Boot + Evolution API
  key_name      = aws_key_pair.autocloser_key.key_name

  vpc_security_group_ids = [aws_security_group.autocloser_sg.id]

  # Script d'initialisation (User Data) pour installer Docker et lancer l'app
  user_data = <<-EOF
              #!/bin/bash
              apt-get update -y
              apt-get install -y docker.io docker-compose git curl
              systemctl start docker
              systemctl enable docker
              usermod -aG docker ubuntu

              # Création du dossier d'app
              mkdir -p /home/ubuntu/autocloser
              cd /home/ubuntu/autocloser

              # Téléchargement du docker-compose depuis le dépôt Git public
              curl -o docker-compose-ec2.yml https://raw.githubusercontent.com/MalickDevWeb/Response-AI-Bussness/main/Mon_projet/autocloser-workspace/docker-compose-ec2.yml

              # Remplacement de l'IP publique dynamiquement dans le fichier
              PUBLIC_IP=$(curl http://checkip.amazonaws.com)
              sed -i "s/VOTRE_IP_EC2_PUBLIQUE/$PUBLIC_IP/g" docker-compose-ec2.yml

              # Lancement de l'infrastructure
              docker-compose -f docker-compose-ec2.yml up -d
              EOF

  tags = {
    Name = "AutoCloser-Production"
  }
}
