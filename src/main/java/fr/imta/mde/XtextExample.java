package fr.imta.mde;

import fr.imta.mde.book.BookFactory;

public class XtextExample {
    public static void main(String[] args) {
        var factory = BookFactory.eINSTANCE;

        var author = factory.createAuthor();
        author.setName("Inio Asano");

        var book1 = factory.createBook();
        book1.setTitle("Oyasumi Punpun");
        book1.setPageNumber(123);
        book1.setAuthor(author);

        var book2 = factory.createBook();
        book2.setTitle("Dead Dead Demon's Dededededestruction");
        book2.setPageNumber(432);
        book2.setAuthor(author);

        System.out.println("Books written by " + author.getName() + ":");
        for (var b : author.getBooks()) {
            System.out.println(b.getTitle());
        }
    }
}