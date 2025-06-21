package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Servis za logovanje događaja
 * </pre>
 */

@io.grpc.stub.annotations.GrpcGenerated
public final class EventLoggingServiceGrpc {

  private EventLoggingServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "events.EventLoggingService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SystemEvent,
          LogEventResponse> getLogEventMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "LogEvent",
      requestType = SystemEvent.class,
      responseType = LogEventResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SystemEvent,
          LogEventResponse> getLogEventMethod() {
    io.grpc.MethodDescriptor<SystemEvent, LogEventResponse> getLogEventMethod;
    if ((getLogEventMethod = EventLoggingServiceGrpc.getLogEventMethod) == null) {
      synchronized (EventLoggingServiceGrpc.class) {
        if ((getLogEventMethod = EventLoggingServiceGrpc.getLogEventMethod) == null) {
          EventLoggingServiceGrpc.getLogEventMethod = getLogEventMethod =
              io.grpc.MethodDescriptor.<SystemEvent, LogEventResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "LogEvent"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SystemEvent.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  LogEventResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EventLoggingServiceMethodDescriptorSupplier("LogEvent"))
              .build();
        }
      }
    }
    return getLogEventMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EventLoggingServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceStub>() {
        @java.lang.Override
        public EventLoggingServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EventLoggingServiceStub(channel, callOptions);
        }
      };
    return EventLoggingServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EventLoggingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceBlockingStub>() {
        @java.lang.Override
        public EventLoggingServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EventLoggingServiceBlockingStub(channel, callOptions);
        }
      };
    return EventLoggingServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EventLoggingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EventLoggingServiceFutureStub>() {
        @java.lang.Override
        public EventLoggingServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EventLoggingServiceFutureStub(channel, callOptions);
        }
      };
    return EventLoggingServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Servis za logovanje događaja
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void logEvent(SystemEvent request,
                          io.grpc.stub.StreamObserver<LogEventResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getLogEventMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service EventLoggingService.
   * <pre>
   * Servis za logovanje događaja
   * </pre>
   */
  public static abstract class EventLoggingServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return EventLoggingServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service EventLoggingService.
   * <pre>
   * Servis za logovanje događaja
   * </pre>
   */
  public static final class EventLoggingServiceStub
      extends io.grpc.stub.AbstractAsyncStub<EventLoggingServiceStub> {
    private EventLoggingServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventLoggingServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventLoggingServiceStub(channel, callOptions);
    }

    /**
     */
    public void logEvent(SystemEvent request,
                         io.grpc.stub.StreamObserver<LogEventResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getLogEventMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service EventLoggingService.
   * <pre>
   * Servis za logovanje događaja
   * </pre>
   */
  public static final class EventLoggingServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<EventLoggingServiceBlockingStub> {
    private EventLoggingServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventLoggingServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventLoggingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public LogEventResponse logEvent(SystemEvent request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getLogEventMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service EventLoggingService.
   * <pre>
   * Servis za logovanje događaja
   * </pre>
   */
  public static final class EventLoggingServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<EventLoggingServiceFutureStub> {
    private EventLoggingServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected EventLoggingServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EventLoggingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<LogEventResponse> logEvent(
        SystemEvent request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getLogEventMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LOG_EVENT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LOG_EVENT:
          serviceImpl.logEvent((SystemEvent) request,
              (io.grpc.stub.StreamObserver<LogEventResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getLogEventMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
                    SystemEvent,
                    LogEventResponse>(
                service, METHODID_LOG_EVENT)))
        .build();
  }

  private static abstract class EventLoggingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EventLoggingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return SystemEventsProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EventLoggingService");
    }
  }

  private static final class EventLoggingServiceFileDescriptorSupplier
      extends EventLoggingServiceBaseDescriptorSupplier {
    EventLoggingServiceFileDescriptorSupplier() {}
  }

  private static final class EventLoggingServiceMethodDescriptorSupplier
      extends EventLoggingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    EventLoggingServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (EventLoggingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EventLoggingServiceFileDescriptorSupplier())
              .addMethod(getLogEventMethod())
              .build();
        }
      }
    }
    return result;
  }
}
