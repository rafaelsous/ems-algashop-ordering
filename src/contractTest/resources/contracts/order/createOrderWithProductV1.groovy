package contracts.order

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

Contract.make {
    request {
        method POST()
        headers {
            contentType("application/vnd.order-with-product.v1+json")
        }
        urlPath("/api/v1/orders") {
            body([
                    customerId   : value(test(anyUuid()), stub(anyUuid())),
                    productId    : value(test(anyUuid()), stub(anyUuid())),
                    quantity     : value(test(1), stub(anyPositiveInt())),
                    paymentMethod: "GATEWAY_BALANCE",
                    shipping     : [
                            recipient: [
                                    firstName: "John",
                                    lastName : "Doe",
                                    document : "12345",
                                    phone    : "5511912341234"
                            ],
                            address  : [
                                    street      : "Bourbon Street",
                                    number      : "2000",
                                    complement  : "apt 122",
                                    neighborhood: "North Ville",
                                    city        : "Yostfort",
                                    state       : "South Carolina",
                                    zipCode     : "12321"
                            ]
                    ],
                    billing      : [
                            firstName: "John",
                            lastName : "Doe",
                            document : "12345",
                            email    : "john.doe@email.com",
                            phone    : "5511912341234",
                            address  : [
                                    street      : "Bourbon Street",
                                    number      : "2000",
                                    complement  : "apt 122",
                                    neighborhood: "North Ville",
                                    city        : "Yostfort",
                                    state       : "South Carolina",
                                    zipCode     : "12321"
                            ]
                    ]
            ])
        }
    }
    response {
        status HttpStatus.CREATED.value()
        headers {
            contentType MediaType.APPLICATION_JSON_VALUE
        }
        body([
                id           : anyNonBlankString(),
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
                status       : "PLACED",
                paymentMethod: "GATEWAY_BALANCE",
                shipping     : [
                        cost        : 20.5,
                        expectedDate: anyDate(),
                        recipient   : [
                                firstName: "John",
                                lastName : "Doe",
                                document : "12345",
                                phone    : "5511912341234"
                        ],
                        address     : [
                                street      : "Bourbon Street",
                                number      : "2000",
                                complement  : "apt 122",
                                neighborhood: "North Ville",
                                city        : "Yostfort",
                                state       : "South Carolina",
                                zipCode     : "12321"
                        ]
                ],
                billing      : [
                        firstName: "John",
                        lastName : "Doe",
                        document : "12345",
                        phone    : "5511912341234",
                        address  : [
                                street      : "Bourbon Street",
                                number      : "2000",
                                complement  : "apt 122",
                                neighborhood: "North Ville",
                                city        : "Yostfort",
                                state       : "South Carolina",
                                zipCode     : "12321"
                        ]
                ],
                items        : [
                        [
                                id         : anyNonBlankString(),
                                orderId    : anyNonBlankString(),
                                productId  : anyUuid(),
                                price      : 19.99,
                                productName: "Notebook Dive Gamer X11",
                                quantity   : 2,
                                totalAmount: 41.98
                        ]
                ]
        ])
    }
}
