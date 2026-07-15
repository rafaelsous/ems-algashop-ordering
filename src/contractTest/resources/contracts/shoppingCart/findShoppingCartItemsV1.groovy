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
        url("/api/v1/customers/me/shopping-cart/items")
    }
    response {
        status HttpStatus.OK.value()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body(
                items: [
                        [
                                id         : anyUuid(),
                                productId  : anyUuid(),
                                name       : "Mouse pad",
                                price      : 100.00,
                                quantity   : 1,
                                totalAmount: 100.00,
                                available  : anyBoolean(),
                        ],
                        [
                                id         : anyUuid(),
                                productId  : anyUuid(),
                                name       : "4G RAM",
                                price      : 150.00,
                                quantity   : 1,
                                totalAmount: 150.00,
                                available  : anyBoolean(),
                        ]
                ]
        )
    }
}