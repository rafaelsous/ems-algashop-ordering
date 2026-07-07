package contracts.order


import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method GET()
        headers {
            accept MediaType.APPLICATION_JSON_VALUE
        }
        url("/api/v1/customers/me/orders") {
            queryParameters {
                parameter("size", value(stub(optional(anyNumber())), test(10)))
                parameter("page", value(stub(optional(anyNumber())), test(0)))
            }
        }
    }
    response {
        status HttpStatus.OK.value()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body([
                size         : 1,
                number       : 0,
                totalPages   : 1,
                totalElements: 1,
                content      : [
                        [
                                id           : "0N7ZHVJXN94S6",
                                customer     : [
                                        id       : anyUuid(),
                                        firstName: "John",
                                        lastName : "Doe",
                                        document : "12345",
                                        email    : "john.doe@email.com",
                                        phone    : "1191234564"
                                ],
                                totalItems   : 2,
                                totalAmount  : 41.98,
                                placedAt     : anyIso8601WithOffset(),
                                paidAt       : null,
                                canceledAt   : null,
                                readyAt      : null,
                                status       : "PLACED",
                                paymentMethod: "GATEWAY_BALANCE"
                        ]
                ]
        ])
    }
}