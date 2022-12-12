package org.medium.user.service;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.medium.grpcserver.Image;
import org.medium.grpcserver.ImageServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class GrpcService {

    @GrpcClient("cloud-grpc-server")
    private ImageServiceGrpc.ImageServiceBlockingStub imageStub;

    public String getAvatarUrl(String username, String firstName, String lastName) {
        Image.imageRequest request = Image.imageRequest.newBuilder()
                .setUsername(username)
                .setFirstName(firstName)
                .setLastName(lastName)
                .build();
        final Image.imageResponse response = imageStub.image(request);
        return response.getAvatarUrl();
    }
}
