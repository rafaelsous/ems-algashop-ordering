package contracts.order

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept "application/json"
        }
        url("/api/v1/customers/me/orders/0N8N9TJWPSBWK")
    }
    response {
        status NOT_FOUND()
        headers {
            contentType("application/problem+json")
        }
        body([
                type: "/errors/not-found",
                title: "Not found",
                detail: "Order 0N8N9TJWPSBWK not found",
                instance: fromRequest().path(),
                timestamp: iso8601WithOffset()
        ])
    }
}