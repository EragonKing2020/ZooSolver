package mde;

import book.BookFactory;

public class XCoreExample {
    public static void main(String[] args) {
        var factory = BookFactory.eINSTANCE;

        // création d'un objet en utilisant la factory
        var asano = factory.createAuthor();
        // il existe des getters/setters pour chaque attribut mis dans le 
        asano.setName("Inio Asano");

        var punpun = factory.createBook();
        punpun.setTitle("Oyasumi Punpun");
        punpun.setPageNumber(123);
        punpun.setAuthor(asano);

        var dedede = factory.createBook();
        dedede.setTitle("Dead Dead Demon's Dededededestruction");
        dedede.setPageNumber(432);
        asano.getBooks().add(dedede);

        System.out.println("Books written by " + asano.getName() + ":");
        for (var b : asano.getBooks()) {
            System.out.println(b.getTitle());
        }
    }
}