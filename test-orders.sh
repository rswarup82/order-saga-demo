#!/bin/bash

# Order Saga Demo - Test Script
# This script creates sample orders and monitors their status

API_URL="http://localhost:8181/api/orders"

echo "🚀 Order Saga Demo - Testing Script"
echo "===================================="
echo ""

# Function to create an order
create_order() {
    local file=$1
    local name=$2
    
    echo "📦 Creating order: $name"
    response=$(curl -s -X POST $API_URL \
        -H "Content-Type: application/json" \
        -d @$file)
    
    order_id=$(echo $response | jq -r '.orderId')
    echo "✅ Order created: $order_id"
    echo ""
    
    echo "$order_id"
}

# Function to check order status
check_order() {
    local order_id=$1
    
    echo "🔍 Checking order status: $order_id"
    curl -s $API_URL/$order_id | jq '.'
    echo ""
}

# Function to list all orders
list_all_orders() {
    echo "📋 All Orders:"
    curl -s $API_URL | jq '.[] | {orderId, customerId, status, totalAmount}'
    echo ""
}

# Main execution
echo "Creating sample orders..."
echo ""

# Create order 1
ORDER1=$(create_order "sample-requests/order-electronics.json" "Electronics Order")
sleep 2

# Create order 2
ORDER2=$(create_order "sample-requests/order-sports.json" "Sports Order")
sleep 2

# Create order 3
ORDER3=$(create_order "sample-requests/order-office.json" "Office Furniture Order")
sleep 2

echo "⏳ Waiting for workflows to complete (15 seconds)..."
sleep 15

echo ""
echo "📊 Order Status Report"
echo "======================"
echo ""

# Check status of all created orders
check_order $ORDER1
check_order $ORDER2
check_order $ORDER3

# List all orders
list_all_orders

echo "✨ Test complete!"
echo ""
echo "💡 Tips:"
echo "  - View workflows in Temporal UI: http://localhost:8088"
echo "  - Check H2 database: http://localhost:8080/h2-console"
echo "  - Create more orders by running: curl -X POST $API_URL -H 'Content-Type: application/json' -d @sample-requests/order-electronics.json"
