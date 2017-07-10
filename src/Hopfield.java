import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

/*
Класс Hopfield реализует нейронную сеть Хопфилда с синхронным режимом работы,
в качестве функции активации используется гиперболический тангенс. Для графического
интерфейса используется библиотека Swing.
*/
public class Hopfield {

    private Renderer renderer = new Renderer();
    private JTable table;
    private TableModel tm;
    private TableColumn tc;
    private DefaultTableModel dtm;
    private JPanel panel;
    private JScrollPane pane;
    private JFrame frame;
    private JPanel panelBackground;
    private JTextArea area;
    private String image[][];
    private LinkedList<int[][]> forms;
    private int sizeListForms = -1;
    private int numOfForms = 0;
    private int countOfForms =0;

    public Hopfield(){

        image = new String[7][7];
        forms = new LinkedList<int[][]>();

        //окно
        frame = new JFrame("Нейронная сеть Хопфилда");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setBackground(Color.GRAY);

        //создаем заднюю панель
        panelBackground = new JPanel();
        panelBackground.setSize(700,500);
        panelBackground.setLayout(null);
        frame.add(panelBackground);

        // Таблица
        JTable table = new JTable();
        //Создаем таблицу 
        createDefaultTableModel();
        //устанавливаем размер высоты/ширины для ячеек таблицы
        setSizeTable();
        //Создаем панель 
        createPanel(new Rectangle(20,20,340,330));
        //создаем панель для кнопок
        JPanel panel1 = new JPanel();
        panel1.setBounds(430,20,200,200);
        panel1.setLayout(null);
        //создаем кнопки
        JButton buttonAdd = new JButton("Добавить образ");
        buttonAdd.setBackground(Color.LIGHT_GRAY);
        buttonAdd.setBounds(15,5,180,25);

        JButton buttonCount = new JButton("Посмотреть все образы");
        buttonCount.setBounds(15, 35, 180, 25);
        buttonCount.setBackground(Color.LIGHT_GRAY);

        JButton buttonDelete = new JButton("Удалить все образы");
        buttonDelete.setBounds(15,65,180,25);
        buttonDelete.setBackground(Color.LIGHT_GRAY);

        JButton buttonFind = new JButton("Распознать образ");
        buttonFind.setBounds(15,95,180,25);
        buttonFind.setBackground(Color.LIGHT_GRAY);

        JButton buttonClean = new JButton("Очистить поле");
        buttonClean.setBounds(15,125,180,25);
        buttonClean.setBackground(Color.LIGHT_GRAY);

        //добавляем слушателей к кнопкам
        buttonAdd.addActionListener(new addButton());
        panel1.add(buttonAdd);
        buttonCount.addActionListener(new countButton());
        panel1.add(buttonCount);
        buttonDelete.addActionListener(new deleteButton());
        panel1.add(buttonDelete);
        buttonFind.addActionListener(new checkButton());
        panel1.add(buttonFind);
        buttonClean.addActionListener(new cleanButton());
        panel1.add(buttonClean);

        panelBackground.add(panel1);
        area = new JTextArea();
        area.setBorder(BorderFactory.createLineBorder(Color.darkGray,1,false));
        area.setBounds(20,360,300,80);
        area.setText("Нейронная сеть: сеть Хопфилда" + "\n" + "Режим работы сети: синхронный" + "\n" +
                "Размер изображения: 7х7" + "\n" + "Функция активации: гиперболический тангенс");
        area.setEditable(false);
        panelBackground.add(area);
        frame.setVisible(true);
    }

    //настройки таблицы
    private void setSizeTable(){
        table.setRowHeight(45);
        for (int i = 0; i < tm.getColumnCount(); i++) {
            tc = table.getColumnModel().getColumn(i);
            tc.setPreferredWidth(45);
        }
    }

    private void createDefaultTableModel(){
        dtm = new DefaultTableModel(7, 7);
        table = new JTable(dtm){
            @Override
            public boolean isCellEditable(int arg0, int arg1) {
                return false;
            }
        };
        table.isCellEditable(7,7); //!
        table.setCellSelectionEnabled(false);
        tm = table.getModel();
    }

    private void createPanel(Rectangle rec){
        panel = new JPanel();
        pane = new JScrollPane(panel);
        createPane();
        pane.setBounds(rec);
        panelBackground.add(pane);
    }

    //тут отрисовка
    private void createPane(){
        //создаем панель с ползунком
        panel.setBorder(BorderFactory.createLineBorder(Color.darkGray,1,false));
        for(int i=0;i<7;i++){
            for(int j=0;j<7;j++){
                image[i][j] = " ";
                table.setValueAt(image[i][j],i,j);
            }
        }
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int y= table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                int x =table.getSelectionModel().getLeadSelectionIndex();

                if(image[x][y].equals(" ")){
                    image[x][y] ="  ";
                }
                else{
                    image[x][y] =" ";
                }
                for(int i=0;i<7;i++){
                    for(int j=0;j<7;j++){
                        table.setValueAt(image[i][j],i,j);
                    }
                }
                for (int i = 0; i < table.getColumnCount(); i++) {
                    table.getColumnModel().getColumn(i).setCellRenderer(renderer);
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        panel.add(table);
    }

    //кнопка Добавить образ
    private class addButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            int matr[][] = new int[7][7];
            sizeListForms++;
            numOfForms++;

            for(int i=0;i<7;i++){
                for(int j=0;j<7;j++){
                    if(table.getValueAt(i,j).toString().equals("  ")){
                        matr[i][j] = 1;
                    }
                    else {
                        matr[i][j] = -1;
                    }
                }
            }
            forms.add(sizeListForms,matr);
            area.setText("");
            area.setText("Образ добавлен" + "\n" + "Всего образов: " + numOfForms);
        }
    }

    //кнопка Посмотреть все образы
    private class countButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(forms.isEmpty()){
                area.setText("Список образов пуст, добавьте образы");
            }
            else{
                int[][] matr;
                if (countOfForms == numOfForms) {
                    countOfForms = 0;
                }
                matr = forms.get(countOfForms);
                for (int i = 0; i < 7; i++) {
                    for (int j = 0; j < 7; j++) {
                        if(matr[i][j] == 1){
                            table.setValueAt("  ", i, j);
                        }
                        else{
                            table.setValueAt(" ", i, j);
                        }
                    }
                }
                //рисуем
                for (int i = 0; i < table.getColumnCount(); i++) {
                    table.getColumnModel().getColumn(i).setCellRenderer(renderer);
                }
                countOfForms++;
                area.setText("Образ № " + countOfForms + " (из " + numOfForms + ")");
            }
        }
    }

    //кнопка очистить поле
    private class cleanButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            area.setText("");
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    image[i][j] = " ";
                    table.setValueAt(image[i][j], i, j);
                }
            }
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
    }

    //кнопка удалить все образы
    private class deleteButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            forms.clear();
            sizeListForms= -1;
            countOfForms =0;
            numOfForms =0;

            for(int i=0;i<7;i++){
                for(int j=0; j<7; j++){
                    image[i][j] = " ";
                    table.setValueAt(image[i][j], i, j);
                }
            }
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
            area.setText("Все образы удалены.");
        }
    }

    //кнопка Распознать образ
    private class checkButton implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(numOfForms==0){
                area.setText("Список образов пуст. Добавьте образы.");
            }
            else{
                area.setText("Проверяем...");
                //переводим массивы в вектора
                LinkedList<int[][]> listVectors = new LinkedList<int[][]>();
                for(int i=0;i<numOfForms;i++){
                    listVectors.add(i,toVector(forms.get(i)));
                }
                // транспонируем матрицы
                LinkedList<int[][]> listTransposeVectors = new LinkedList<int[][]>();
                for(int i=0;i<numOfForms;i++){
                    listTransposeVectors.add(i,transpose(listVectors.get(i)));
                }
                //умножим trans на вектор
                LinkedList<int[][]> listProduct = new LinkedList<int[][]>();
                for(int i=0;i<numOfForms;i++){
                    listProduct.add(i,product(listTransposeVectors.get(i),listVectors.get(i)));
                }
                //суммируем все результаты умножения
                int[][] W = new int[49][49];
                for(int i=0;i<49;i++){
                    for(int j=0;j<49;j++){
                        W[i][j] = 0;
                    }
                }
                for(int i=0;i<numOfForms;i++){
                    W = sum(W,listProduct.get(i));
                }
                //обнулим диагональ
                for(int i=0;i<49;i++){
                    for(int j=0;j<49;j++){
                        if(i==j)
                            W[i][j]=0;
                    }
                }
                //возьмем вектор, который будем проверять
                int matr[][] = new int[7][7];

                for(int i=0;i<7;i++){
                    for(int j=0;j<7;j++){
                        if(table.getValueAt(i,j).toString().equals("  ")){
                            matr[i][j]=1;
                        }else{
                            matr[i][j] = -1;
                        }
                    }
                }
                int[][] vect = toVector(matr);
                //транспонируем вектор
                int[][] transpVector = transpose(vect);
                //перемножим матрицу весов с транс вектором
                int[][] y= product(W,transpVector);
                //используем функцию активации
                int[][] res;

                res = activity(y);
                //проверяем, совпадает ли конечный вектор с эталонами
                boolean isTrue = false;
                int toPercent =0;
                for(int i=0;i<numOfForms;i++) {
                    if (toEquals(res, listTransposeVectors.get(i))) {
                        isTrue = true;
                        toPercent = inPercentages(listTransposeVectors.get(i),transpVector);
                        area.setText("Искаженный образ похож на "+(i+1)+" образ"+"\n"+"Уровень искажения: "+(toPercent)+"%"+"\n");
                        break;
                    }
                }
                if(!isTrue) {
                    while (toEquals(y, res)) {
                        y = product(W, res);
                        res = activity(y);
                    }
                    //когда вход и выход совпадет, проверяем результат с эталонами
                    for(int i=0;i<numOfForms;i++) {
                        if (toEquals(res, listTransposeVectors.get(i))) {
                            isTrue = true;
                            toPercent = inPercentages(listTransposeVectors.get(i),transpVector);
                            area.setText("Искаженный образ похож на "+(i+1)+" образ"+"\n"+"Уровень искажения: "+toPercent+"%"+"\n");
                            break;
                        }
                        else{
                            area.setText("Совпадений не найдено." );
                        }
                    }
                }
            }

        }
    }
    //перевод двумерного массива в вектор
    private int[][] toVector(int mas[][]){
        int vector[][] = new int[1][49];
        int count =0;
        for(int i=0; i<7;i++){
            for(int j=0; j<7;j++){
                vector[0][count] = mas[i][j];
                count++;
            }
        }
        return vector;
    }
    //транспонирование вектора
    private int[][] transpose(int vector[][]){
        int[][] transp = new int[49][1];
        for(int i=0;i<49;i++){
            transp[i][0] = vector[0][i];
        }
        return transp;
    }
    //умножение двух массивов(или векторов)
    private int[][] product(int[][] matr1, int[][] matr2){
        int[][] prod = new int[matr1.length][matr2[0].length];
        for(int i = 0; i < matr1.length; i++){
            for(int j = 0; j < matr2[0].length; j++){
                for(int k = 0; k < matr2.length; k++){
                    prod[i][j] += matr1[i][k] * matr2[k][j];
                }
            }
        }
        return prod;
    }
    //суммирование матриц
    private int[][] sum(int[][] matr1, int[][] matr2){
        int[][] res = new int[49][49];
        for(int i = 0; i < 49; i++){
            for(int j = 0; j < 49; j++){
                res[i][j] = matr1[i][j] + matr2[i][j];
            }
        }
        return res;
    }
    //функция активации
    private int[][] activity(int[][] mas){
        int[][] res = new int[49][1];
        for(int i=0; i<49;i++){
            double d= (( Math.pow(Math.E,Double.parseDouble(String.valueOf(mas[i][0]))) -
                    Math.pow(Math.E,-Double.parseDouble(String.valueOf(mas[i][0]))) ) /
                    ( Math.pow(Math.E,Double.parseDouble(String.valueOf(mas[i][0]))) +
                            Math.pow(Math.E,-Double.parseDouble(String.valueOf(mas[i][0])))));
            d = Math.round(d);
            res[i][0] = (int)d ;

        }
        return res;
    }

    private boolean toEquals(int[][] mas1, int[][] mas2){
        for(int i=0;i<49;i++){
            if(mas1[i][0] != mas2[i][0])
                return false;
        }
        return true;
    }
    //Сравнение искаженного образа и найденного эталонного образа в процентах
    private int inPercentages(int[][] cool, int[][] distorted){
        int count =0;
        for(int i=0;i<49;i++){
            if(cool[i][0] != distorted[i][0]){
                count++;
            }
        }
        double res=( 100 * count ) / 49.0;
        int result = (int)Math.round(res);
        return result;
    }
}
