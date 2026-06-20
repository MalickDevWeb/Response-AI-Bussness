output "server_public_ip" {
  description = "IP publique de l'instance EC2 AutoCloser"
  value       = aws_instance.autocloser_server.public_ip
}

output "server_public_dns" {
  description = "DNS public de l'instance EC2"
  value       = aws_instance.autocloser_server.public_dns
}

output "evolution_api_url" {
  description = "URL d'accès à Evolution API (WhatsApp Gateway)"
  value       = "http://${aws_instance.autocloser_server.public_ip}:8080"
}

output "spring_boot_api_url" {
  description = "URL d'accès au Backend Spring Boot"
  value       = "http://${aws_instance.autocloser_server.public_ip}:3000"
}

output "ssh_connection_command" {
  description = "Commande SSH pour se connecter à l'instance"
  value       = "ssh -i ~/.ssh/autocloser_key.pem ubuntu@${aws_instance.autocloser_server.public_ip}"
}
