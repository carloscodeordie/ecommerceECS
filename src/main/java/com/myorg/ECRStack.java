package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecr.RepositoryProps;
import software.amazon.awscdk.services.ecr.TagMutability;
import software.constructs.Construct;

public class ECRStack extends Stack {

    private final Repository productsServiceRepository;

    public ECRStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.productsServiceRepository =
                new Repository(
                        this,
                        "ProductsService",
                        RepositoryProps.builder()
                                .repositoryName("productsservice")
                                .removalPolicy(RemovalPolicy.DESTROY)
                                .imageTagMutability(TagMutability.IMMUTABLE)
                                .emptyOnDelete(true)
                                .build()
                );
    }

    public Repository getProductsServiceRepository() {
        return this.productsServiceRepository;
    }
}
