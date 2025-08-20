package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.HashMap;
import java.util.Map;

public class EcommerceEcsApp {

    public static void main(final String[] args) {
        App app = new App();

        String accountId = "140147588980";
        String region = "us-east-2";

        Environment environment = Environment.builder()
                .account(accountId)
                .region(region)
                .build();

        Map<String, String> infraTags = new HashMap<String, String>();
        infraTags.put("team", "codeordie");
        infraTags.put("cost", "ECommerceInfra");

        ECRStack ecrStack = new ECRStack(
                app,
                "Ecr",
                StackProps.builder()
                        .env(environment)
                        .tags(infraTags)
                        .build()
        );

        VpcStack vpcStack = new VpcStack(
                app,
                "Vpc",
                StackProps.builder()
                        .env(environment)
                        .tags(infraTags)
                        .build()
        );

        ClusterStack clusterStack = new ClusterStack(
                app,
                "Cluster",
                StackProps.builder()
                        .env(environment)
                        .tags(infraTags)
                        .build(),
                new ClusterStackProps(vpcStack.getVpc())
        );
        clusterStack.addDependency(vpcStack);

        NlbStack nlbStack = new NlbStack(
                app,
                "Nlb",
                StackProps.builder()
                        .env(environment)
                        .tags(infraTags)
                        .build(),
                new NlbStackProps(vpcStack.getVpc())
        );
        nlbStack.addDependency(vpcStack);

        Map<String, String> productServiceTags = new HashMap<String, String>();
        productServiceTags.put("team", "codeordie");
        productServiceTags.put("cost", "ECommerceProductsService");

        ProductsServiceStack productsServiceStack = new ProductsServiceStack(
                app,
                "ProductsService",
                StackProps.builder()
                        .env(environment)
                        .tags(productServiceTags)
                        .build(),
                new ProductsServiceProps(
                        vpcStack.getVpc(),
                        clusterStack.getCluster(),
                        nlbStack.getNetworkLoadBalancer(),
                        nlbStack.getApplicationLoadBalancer(),
                        ecrStack.getProductsServiceRepository()
                )
        );
        productsServiceStack.addDependency(vpcStack);
        productsServiceStack.addDependency(clusterStack);
        productsServiceStack.addDependency(nlbStack);
        productsServiceStack.addDependency(ecrStack);

        ApiStack apiStack = new ApiStack(
                app,
                "Api",
                StackProps.builder()
                        .env(environment)
                        .tags(infraTags)
                        .build(),
                new ApiStackProps(
                        nlbStack.getVpcLink(),
                        nlbStack.getNetworkLoadBalancer()
                )
        );
        apiStack.addDependency(nlbStack);

        app.synth();
    }
}

