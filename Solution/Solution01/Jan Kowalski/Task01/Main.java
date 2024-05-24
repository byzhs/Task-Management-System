import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public
    class Main {

    public static void main(String[] args) {
        new Object(){
            public int getValue(){
                return 127;
            }
        }.getValue();

        Object obj = new Object();

        System.out.println(obj.toString());

        obj = new Object(){
            @Override
            public String toString() {
                return "akuku";
            }
        };
        System.out.println(obj.toString());

//        Object obj = new Object(){
//            public int getValue(){
//                return 127;
//            }
//        };
//        obj.getValue();

        var o = new Object(){
            public int getValue(){
                return 127;
            }
        };
        o.getValue();

// ====================================

        Figure fig = new Square(5);
        System.out.println(
            fig.getArea()
        );

        new AbstractBase(){
            @Override
            public void method1() {

            }

            @Override
            public void method2() {

            }

            @Override
            public void method3() {
                System.out.println("tu3");
            }

            @Override
            public void method4() {

            }
        };

        new AbstractBaseAdapter(){
            @Override
            public void method3() {
                System.out.println("tu");
            }
        };

//======================================

        Frame frame = new Frame();
        frame.setSize(640, 480);
        frame.setVisible(true);

        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };

        frame.addWindowListener(windowAdapter);

    }
}