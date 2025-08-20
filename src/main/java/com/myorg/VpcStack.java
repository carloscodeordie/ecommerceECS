package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcProps;
import software.constructs.Construct;

public class VpcStack extends Stack {
    private Vpc vpc;

    public VpcStack(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);

        this.vpc = new Vpc(
                this,
                "Vpc",
                VpcProps.builder()
                        .vpcName("ECommerceVPC")
                        .maxAzs(2)
                        // Remove this for production, it means NAT Gateway will not be used
                        // .natGateways(0)
                        .build()
        );
    }

    public Vpc getVpc() {
        return this.vpc;
    }
}
