package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method POST()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        urlPath("/api/v1/customers/me/shopping-cart/items") {
            body([
                    productId     : value(test(anyUuid()), stub(anyUuid())),
                    quantity      : value(test(anyPositiveInt()), stub(anyPositiveInt()))
            ])
        }
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}