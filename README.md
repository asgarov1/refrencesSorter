# IEEE References Sorter

## About
According to IEEE Citation standard, citations should be sorted in the order of appearance in text. I personally often
find myself working on the assignment in non sequential order, resulting in non sequential references. In order to
avoid manual work of resorting citations myself - this program does it for me.

## Usage
 - Place your word document in one of the project's folders, for example `src/main/resources/your_document.docx`
 - Run it from [Runner](src/main/java/com/asgarov/references_sorter/Runner.java) class, supplying the above mentioned path
 as the only parameter:

 ```
 public class Runner {
     public static void main(String[] args) {
         MicrosoftWordSorter.sort("src/main/resources/your_document.docx");
     }
 }
 ```

The updated word document will be placed in the same folder with modified name, in this case `src/main/resources/your_document_updated.docx`

### Considerations
 - You have to close the document before running the program so that java can access it - otherwise you'll get
`java.io.FileNotFoundException: [provided_path_to_document] (The process cannot access the file because it is being used by another process)`
 - Program expects that there is a heading with value `References` - otherwise you will get
`java.lang.IllegalStateException: Can't find references: File doesn't contain a heading with value "References"!`
 - Program expects that the references are the last thing in the document and will overwrite everything after `References` heading
with the new list of sorted references.