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
        urlPath("/api/v1/customers/me/shopping-cart")
    }
    response {
        status HttpStatus.CREATED.value()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body([
                id         : anyUuid(),
                customerId : anyUuid(),
                totalItems : 2,
                totalAmount: 250.00,
                items      : [
                        [
                                productId  : anyUuid(),
                                name       : "Mouse pad",
                                price      : 100.00,
                                quantity   : 1,
                                totalAmount: 100.00,
                                available  : anyBoolean(),
                        ],
                        [
                                productId  : anyUuid(),
                                name       : "4G RAM",
                                price      : 150.00,
                                quantity   : 1,
                                totalAmount: 150.00,
                                available  : anyBoolean(),
                        ]
                ]
        ])
    }
}