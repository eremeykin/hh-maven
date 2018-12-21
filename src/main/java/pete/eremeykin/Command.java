package pete.eremeykin;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Command {

    private final String command;
    private final List<Argument> arguments;

    private Command(Builder commandBuilder) {
        this.command = commandBuilder.command;
        this.arguments = new LinkedList<>(commandBuilder.arguments); // Argument is immutable, can do so
    }

    @Override
    public String toString() {
        Stream<String> commandStream = Stream.of(command);
        Stream<String> argumentsStream = arguments.stream().map(Argument::toString);
        return Stream.concat(commandStream, argumentsStream)
                .collect(Collectors.joining(" "));
    }

    String[] asStringArray() {
        String[] resultArray = new String[arguments.size() + 1];
        resultArray[0] = command;
        for (int i = 1; i < arguments.size(); i++) {
            resultArray[i] = arguments.get(i).toString();
        }
        return resultArray;
    }


    static final class Builder {
        private String command;
        private List<Argument> arguments;

        Builder(String command) {
            this.command = command;
            arguments = new LinkedList<>();
        }


        Builder addArgument(String argument) {
            arguments.add(new Argument(argument));
            return this;
        }

        Builder addArgument(String key, String value) {
            arguments.add(new KeyValueArgument(key, value));
            return this;
        }

        Command build() {
            return new Command(this);
        }


    }

    private static class Argument {
        final String key;

        private Argument(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    private final static class KeyValueArgument extends Argument {
        private final String value;

        private KeyValueArgument(String key, String value) {
            super(key);
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }


}
