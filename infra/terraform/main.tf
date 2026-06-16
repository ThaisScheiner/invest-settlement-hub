resource "aws_sqs_queue" "order_created_dlq" {
  name = "order-created-dlq"
}

resource "aws_sqs_queue" "order_created_queue" {
  name = "order-created-queue"

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.order_created_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue" "notification_queue" {
  name = "notification-queue"
}

resource "aws_sqs_queue" "statement_dlq" {
  name = "statement-dlq"
}

resource "aws_sqs_queue" "statement_queue" {
  name = "statement-queue"

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.statement_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sns_topic" "settlement_events" {
  name = "settlement-events"
}

resource "aws_s3_bucket" "investment_statements" {
  bucket = "investment-statements"
}