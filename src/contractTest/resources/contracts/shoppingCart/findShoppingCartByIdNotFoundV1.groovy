package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method GET()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        urlPath("/api/v1/shopping-carts/019b3d32-83b4-716d-ac5f-2a5042bddb61") {
            body([
                    customerId: value(test(anyUuid()), stub(anyUuid()))
            ])
        }
    }
    response {
        status HttpStatus.NOT_FOUND.value()
        headers {
            contentType("application/problem+json")
        }
        body([
                type     : "/errors/not-found",
                title    : "Not found",
                detail   : anyNonBlankString(),
                instance : fromRequest().path(),
                timestamp: iso8601WithOffset()
        ])
    }
}