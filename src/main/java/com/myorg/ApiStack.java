package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.elasticloadbalancingv2.NetworkLoadBalancer;
import software.constructs.Construct;

public class ApiStack extends Stack {
    public ApiStack(final Construct scope, final String id, StackProps props, ApiStackProps apiStackProps) {
        super(scope, id, props);

        RestApi restApi = new RestApi(
                this,
                "RestApi",
                RestApiProps.builder()
                        .restApiName("EcommerceAPI")
                        .build()
        );

        this.createProductsResource(restApi, apiStackProps);
    }

    private void createProductsResource(RestApi restApi, ApiStackProps apiStackProps) {
        // Creates the url for /products endpoints
        Resource productsResource = restApi.getRoot().addResource("products");

        // GET /products
        productsResource.addMethod(
                "GET",
                new Integration(
                        IntegrationProps.builder()
                                // It means it will act as a proxy for AWS VPC Link
                                .type(IntegrationType.HTTP_PROXY)
                                .integrationHttpMethod("GET")
                                // Points to network loader balancer uri
                                .uri("http://" + apiStackProps.networkLoadBalancer().getLoadBalancerDnsName() + ":8080/api/products")
                                .options(
                                        IntegrationOptions.builder()
                                                // Indicates the VPC Link here
                                                .vpcLink(apiStackProps.vpcLink())
                                                .connectionType(ConnectionType.VPC_LINK)
                                                .build()
                                )
                                .build()
                )
        );

    }
}

record ApiStackProps(
        VpcLink vpcLink,
        NetworkLoadBalancer networkLoadBalancer
) {}