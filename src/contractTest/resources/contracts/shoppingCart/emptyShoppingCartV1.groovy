package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method DELETE()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        urlPath("/api/v1/customers/me/shopping-cart/items")
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}