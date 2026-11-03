package rs.ac.uns.isa.transactions.dto;

public class Update {
    public static class Mandatory{
        public record Request(String name){}
        public record Response(Long id, String name){}
    }

    public static class Never{
        public record Request(String name){}
        public record Response(Long id, String name){}
    }
    public static class Required {
        public record Request(String name){}
        public record Response(Long id, String name){}
    }
    public static class Supports {
        public record Request(String name){}
        public record Response(Long id, String name){}
    }
    public static class NotSupported {
        public record Request(String name){}
        public record Response(Long id, String name){}
    }
    public static class Nested {
        public record Request(String name){}
        public record Response(Long id, String name){}
    }
    public static class RequiresNew{
        public record Request(String name){}
        public record Response(Long id, String name){}
    }

}
