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
        urlPath(
                "/api/v1/shopping-carts/019b3d31-f100-78c7-b442-7ac8b336927c/items/019b3d76-6e7c-72ba-a9c6-ddb5b066cc3a"
        )
    }
    response {
        status HttpStatus.NO_CONTENT.value()
    }
}