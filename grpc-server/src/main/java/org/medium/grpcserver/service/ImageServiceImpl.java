package org.medium.grpcserver.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.medium.grpcserver.Image;
import org.medium.grpcserver.ImageServiceGrpc;

@GrpcService
public class ImageServiceImpl extends ImageServiceGrpc.ImageServiceImplBase {

    @Override
    public void image(Image.imageRequest request, StreamObserver<Image.imageResponse> responseObserver) {
        String imageSize = getSize(request.getUsername(), request.getFirstName(), request.getLastName());

        Image.imageResponse image = Image.imageResponse.newBuilder()
                .setAvatarUrl("https://random.imagecdn.app/" + imageSize)
                .build();
        responseObserver.onNext(image);
        responseObserver.onCompleted();
    }

    private String getSize(String username, String firstName, String lastName) {
        final int[] height = {1};
        final int[] width = {1};
        int scale = username.length() % 2 == 0 ? 2 : 1;
        firstName.codePoints().forEach(code -> {
            height[0] = (height[0] * code) % 200;
        });
        lastName.codePoints().forEach(code -> {
            width[0] = (width[0] * code) % 200;
        });
        return width[0] * scale + "/" + height[0] * scale;
    }
}
