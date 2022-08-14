# IEEE References Sorter

## About
According to IEEE Citation standard, citations should be sorted in the order of appearance in text. I personally often
find myself working on the assignment in non sequential order, resulting in non sequential references. In order to
avoid manual work of resorting citations myself - this program does it for me.

## Usage
 - Place your word document in one of the Project folders, for example `src/main/resources/your_document.docx`
 - Run it from [Runner](src/main/java/com/asgarov/references_sorter/Runner.java) class, supplying the above mentioned path
 as the only parameter:

 ```
 public class Runner {
     public static void main(String[] args) {
         MicrosoftWordSorter.sort("src/main/resources/your_document.docx");
     }
 }
 ```

The updated word document will be placed in the same folder with modified name

### Considerations
* Careful! *
 - Program expects that there is a Heading with value `References` - without it program won`t work properly
 - Program expects that References are the last thing in the document and will delete everything after `References` heading
 during the process of sorting