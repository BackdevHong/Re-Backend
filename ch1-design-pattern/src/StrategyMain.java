import strategy.*;

public class StrategyMain {
    static void main() {
        Encoder encoder = new Encoder();

        // base64
        EncodingStrategy base64 = new Base64Strategy();

        // Normal
        NormalStrategy normal = new NormalStrategy();

        String message = "hello java";

        encoder.setEncodingStrategy(base64);
        String base64Result = encoder.getMessage(message);
        System.out.println(base64Result);

        encoder.setEncodingStrategy(normal);
        String normalResult = encoder.getMessage(message);
        System.out.println(normalResult);

        encoder.setEncodingStrategy(new AppendStrategy());
        String appendResult = encoder.getMessage(message);
        System.out.println(appendResult);
    }
}
