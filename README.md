# PDFWatermarkRemover

PDFWatermarkRemover is a Java program designed to remove watermarks from PDF files. It offers various methods for
removing watermarks, which are implemented in classes that implement the WatermarkRemover interface. Currently, there
are several implemented methods:

- **ColorWatermarkRemover**: Removes watermarks of a specific color.
- **PositionWatermarkRemover**: Removes watermarks within a specified rectangular region.
- **RenMichWatermarkRemover**: This remover is not yet complete, as it deals with circular and straight line watermarks
  covering the text making it challenging to remove.

## How it works

The project works by converting PDFs to PNG images into a temporary folder. Then, each image is processed based on the
selected watermark removal method. After processing all the images, they are stitched back together into a single PDF,
and the temporary image folder is removed.

## Usage

It is recommended to use the project with IntelliJ IDEA and the Main.java class. However, please note that the code in
the Main class may need to be adjusted to fit your specific requirements. Below is an example of how the Main class
currently looks:

```java
public class Main {
  static String separator = File.separator;

  public static void main(String[] args) throws Exception {
    String pdfFilePath = "src" + separator + "main" + separator + "resources" + separator + "example.pdf"; // from https://renmich.faculty.wmi.amu.edu.pl/BSM/wyklady/Hasla.pdf
    String tempDirPath = PDFToImages.convertPdfToImages(pdfFilePath);
    WatermarkRemover method = switch (new Random().nextInt(3)) {
      case 0 -> new PositionWatermarkRemover(147, 3053, 821, 3108);
      case 1 -> new ColorWatermarkRemover(Color.BLACK);
      case 2 -> new RenMichWatermarkRemover(9);
      default -> null;
    };
    FileManager.removerWatermarks(method, tempDirPath);
    ImagesToPDF.convertImagesToPDF(tempDirPath, pdfFilePath + ".new.pdf");
    FileManager.removeTempDir(tempDirPath);
  }
}
```

## Resources

In the `resources` folder, you will find a sample PDF file for testing purposes.

## Dependencies

Using IntelliJ IDEA with Maven should automatically download the necessary dependencies (maven).

## Contributing

Feel free to contribute to this project by forking it and submitting pull requests.

## License

This project is licensed under the [MIT License](LICENSE).