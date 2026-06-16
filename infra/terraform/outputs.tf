output "order_created_queue_url" {
  value = aws_sqs_queue.order_created_queue.url
}

output "notification_queue_url" {
  value = aws_sqs_queue.notification_queue.url
}

output "statement_queue_url" {
  value = aws_sqs_queue.statement_queue.url
}

output "statement_dlq_url" {
  value = aws_sqs_queue.statement_dlq.url
}

output "settlement_events_topic_arn" {
  value = aws_sns_topic.settlement_events.arn
}

output "investment_statements_bucket" {
  value = aws_s3_bucket.investment_statements.bucket
}

output "order_created_dlq_url" {
  value = aws_sqs_queue.order_created_dlq.url
}