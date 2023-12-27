package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriterException;
import com.cgvsu.utils.DeletingPolygons;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static com.cgvsu.objwriter.ObjWriter.write;

public class Main {

    public static final String PROGRAM_NAME_IN_HELP = "program (-a | -s | -i <in-file> | -q | -l)";


    private static Model model = null;


    public static void main(String[] args) throws IOException {

        //Path fileName = Path.of("C:/Users/Dan00ile/Documents/CGVSU/3DModels/Faceform/WrapBody.obj");


//-i C:\Users\Dan00ile\Documents\CGVSU\3DModels\Faceform\WrapFemaleBody.obj
//-i C:\Users\Dan00ile\Documents\CGVSU\3DModels\SimpleModelsForReaderTests\cube.obj
//-i C:\Users\Dan00ile\Documents\CGVSU\3DModels\cube.obj
//-e src/output.obj
//-i C:\Users\Dan00ile\Documents\Java\KG_Task3\tests\com\cgvsu\polyremover\testModels\plane2.obj

        Options options = new Options();
        Random random = new Random();


        options.addOption("i", true, "Импорт 3д модели");
        options.addOption("q", "Закончить работу с программой");
        options.addOption("e", true, "Экспортировать модель");
        options.addOption("d", true, "Удалить полигон под номером...");
        options.addOption("m", "Удаление неприкрепленных точек");
        options.addOption("info", "Информация о полигонах");
        options.addOption("r",true, "Удалить рандомно некоторое количество полигонов");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        boolean flagPoints = true;
        boolean flagInfo = true;


        while (true) {
            String nextLine = getInputLine();

            try {
                cmd = parser.parse(options, nextLine.split("\\s+"));
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp(PROGRAM_NAME_IN_HELP, options);
                continue;
            }

            if (cmd.hasOption("i")) {
                Path fileName = Path.of(cmd.getOptionValue("i"));
                String fileContent = Files.readString(fileName);

                try {
                    System.out.println("Загружаем модель...");
                    model = ObjReader.read(fileContent);

                    System.out.println("Вершины: " + model.vertices.size());
                    System.out.println("Вершины текстур: " + model.textureVertices.size());
                    System.out.println("Нормали: " + model.normals.size());
                    System.out.println("Полигоны: " + model.polygons.size());
                    System.out.println("Модель загружена!");
                } catch (ObjWriterException e) {
                    System.out.println("Импорт не произошёл");
                }
            } else if (model == null) {
                System.out.println("Импортируйте модель!");
            } else {
                if (cmd.hasOption("q")) {
                    System.out.println("Работа с моделью окончена.");
                    break;
                }

                if (cmd.hasOption("e")) {
                    String output = cmd.getOptionValue("e");

                    String fileNameOut = output == null ?
                            "src/output.obj" : output;
                    write(fileNameOut, model);

                    System.out.println("Модель была экспортирована в " + fileNameOut);
                }

                if (cmd.hasOption("d")) {
                    String prefix = cmd.getOptionValue("d");

                    List<Integer> arrNum = Arrays.stream(prefix.split(",")).map(Integer::parseInt).collect(Collectors.toList());

                    if (flagPoints) {
                        DeletingPolygons.deletePolyWithDet(model, arrNum);
                    } else {
                        DeletingPolygons.deletePoly(model, arrNum);
                    }


                    System.out.println("Полигон(-ы) удалён(-ы).");
                    if (flagInfo) showPolys();
                }
                if (cmd.hasOption("r")) {
                    String prefix = cmd.getOptionValue("r");

                    List<Integer> nums = new ArrayList<>();

                    for (int i = 0; i < Integer.parseInt(prefix); i++) {
                        nums.add(random.nextInt(model.polygons.size() - 1));
                    }

                    if (flagPoints) {
                        DeletingPolygons.deletePolyWithDet(model, nums);
                    } else {
                        DeletingPolygons.deletePoly(model, nums);
                    }

                    System.out.println("Полигон(-ы) удалён(-ы).");
                    if (flagInfo) showPolys();
                }
                if (cmd.hasOption("info")) {
                    if (flagInfo) {
                        System.out.println("Отображение полигонов выключено");
                        flagInfo = false;
                    } else {
                        System.out.println("Отображение полигонов включено");
                        flagInfo = true;
                    }
                }
                if (cmd.hasOption("m")) {
                    if (flagPoints) {
                        System.out.println("Удаление точек без привязки выключено");
                        flagPoints = false;
                    } else {
                        System.out.println("Удаление точек без привязки включено");
                        flagPoints = true;
                    }
                }
            }

        }
    }

    private static void showPolys() {
        for (int i = 0; i < model.polygons.size(); i++) {
            System.out.print("Полигон #" + i + " - ");
            if (model.polygons.get(i) == null) {
                System.out.println("Удалён");
            } else {
                System.out.println("Не удалён");
            }
        }
    }

    private static String getInputLine() {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
