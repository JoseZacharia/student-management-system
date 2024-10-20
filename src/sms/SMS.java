/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author JOSE BOSS
 */
public class SMS {

    /**
     * @param args the command line arguments
     */
    public static Connection con;  
    public static Statement stmt,stmt2;
    public static ResultSet rs;
    public static String[] arr=new String[50];
    public static String uid,pwd,sid,v;
    
    public static void initialize() throws Exception
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");   
        con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCLE123","SCOTT","tiger");  
        stmt=stmt2=con.createStatement();
    }
    
    public static int tcheck_sid(String sid, String uid) throws Exception
    {
      
        rs=stmt.executeQuery("select count(sid) from student where sid='"+sid+"' and coursecode=(select coursecode from course_subject where subcode=(select subcode from subject where tid='"+uid+"'))");
        rs.next();
        
        return rs.getInt(1);
    }
    
    public static int getmax(String exam) throws SQLException
    {
        rs=stmt.executeQuery("select maxmarks from examname where ename='"+exam+"'");
        rs.next();
        return rs.getInt("maxmarks");
    }
    
    public static int checkatt(int att)
    {
        if(att>=0 & att<=100)
            return 1;
        else 
            return 0;
    }
       
    public static String checkthr(int att)
    {
        if(att>=75)
            return "YES";
        else 
            return "NO";
    }
            
    public static float calc_cgpa(String sid) throws SQLException
    {
        int i,j,max,marks;
        float cgpa=0,totalcgpa=0,weightage;
        DecimalFormat df=new DecimalFormat("###.##");
        rs=stmt.executeQuery("select count(ename) from examname");
        rs.next();
        int ecount = rs.getInt(1);
        String[] exams=new String[ecount];
        //initialize();
        rs=stmt.executeQuery("select ename from examname");
        for(i=0;rs.next();i++)
        {
            //arr[i]=rs.getString(1);
            //System.out.println(i+". "+rs.getString(1));
            exams[i]=rs.getString(1);
            //System.out.println(rs.getString(1));
        }
        rs=stmt.executeQuery("select count(subcode) from course_subject where coursecode=(select coursecode from student where sid='"+sid+"')");
        rs.next();
        int subcount = rs.getInt(1);
        String[] subjects=new String[subcount];
        //initialize();
        rs=stmt.executeQuery("select subname from subject where subcode in (select subcode from course_subject where coursecode=(select coursecode from student where sid='"+sid+"'))");
        for(i=0;rs.next();i++)
        {
            //arr[i]=rs.getString(1);
            //System.out.println(i+". "+rs.getString(1));
            subjects[i]=rs.getString(1);
            //System.out.println(rs.getString(1));
        }
        for(i=0;i<subcount;i++)
        {
            cgpa=0;
            for(j=0;j<ecount;j++)
            {
                max=getmax(exams[j]);//System.out.println("max "+max);
                //System.out.println(subjects[i]+exams[j]);
                rs=stmt.executeQuery("select marks from writes_exam,exam where  writes_exam.examid=exam.examid and sid='"+sid+"' and examname='"+exams[j]+"' and subcode=(select subcode from subject where subname='"+subjects[i]+"')");
                rs.next();
                marks=rs.getInt(1);//System.out.println("Marks "+marks);
                rs=stmt.executeQuery("select weightage from examname where ename='"+exams[j]+"'");
                rs.next();
                weightage=rs.getFloat(1);//System.out.println("Weightage "+weightage);
                //System.out.println(marks/max);
                float a=(float) marks/max;//System.out.println("max/marks "+a);
                float b=a*weightage;//System.out.println("a*weightage "+b);
                //System.out.println("old cgpa "+cgpa);
                cgpa+=a*weightage;//System.out.println("new cgpa "+cgpa);
//                System.out.println("cgpa "+cgpa*10);
                
            }
            
            cgpa=cgpa*10;
//            System.out.println("old tcgpa "+totalcgpa);
            totalcgpa+=cgpa;
//            System.out.println("new tcgpa "+totalcgpa);
//            System.out.println("");
            
        }
        totalcgpa=Float.valueOf(df.format(totalcgpa))/subcount;
        //System.out.println(totalcgpa);
    //        
        return totalcgpa;
        
    }
            
    public static String disptable(ResultSet rs) throws SQLException
    {
        try {
            JTable table = new JTable(login.buildTableModel(rs));
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            table.setFillsViewportHeight(true);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        final JTable jTable= (JTable)e.getSource();
                        final int row = jTable.getSelectedRow();
                        final int column = jTable.getSelectedColumn();
                        final String valueInCell = (String)jTable.getValueAt(row, column);
                        JOptionPane.showMessageDialog(null,valueInCell);
                        
                    }
                    
                }
            });
            JOptionPane.showMessageDialog(null, new JScrollPane(table));
            System.out.println(v);
        } catch (SQLException ex) {
            //Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return v;
        
    }
    
    public static String exammenu() throws Exception
    {
        int i,r,a;
        rs=stmt.executeQuery("select count(ename) from examname");
        rs.next();
        a=rs.getInt(1);
        String[] options=new String[a];
        //initialize();
        rs=stmt.executeQuery("select ename from examname");
        //rs.next();
        for(i=0;rs.next();i++)
        {
            //arr[i]=rs.getString(1);
            //System.out.println(i+". "+rs.getString(1));
            options[i]=rs.getString(1);
            //System.out.println("rs.getString(1)");
        }

        r = JOptionPane.showOptionDialog(null, "Choose the exam", "",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        
        return options[r];
    }
    
    
    public static int specify()
    {
        String[] options = new String[] {"Specify Student", "All Students"};
        int r = JOptionPane.showOptionDialog(null, "Choose an option", "",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        return r;  
    }

    public static String coursemenu() throws SQLException
    {
        int i,r,a;
        rs=stmt.executeQuery("select count(coursename) from course");
        rs.next();
        a=rs.getInt(1);
        String[] options=new String[a];
        //initialize();
        rs=stmt.executeQuery("select coursename from course");
        //rs.next();
        for(i=0;rs.next();i++)
        {
            //arr[i]=rs.getString(1);
            //System.out.println(i+". "+rs.getString(1));
            options[i]=rs.getString(1);
            //System.out.println("rs.getString(1)");
        }
        r = JOptionPane.showOptionDialog(null, "Choose the course", "",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        
        rs=stmt.executeQuery("select coursecode from course where coursename='"+options[r]+"'");
        rs.next();
        return rs.getString(1);
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {
            initialize();
            new login().setVisible(true);
        }
        catch(Exception e){}
    }

   
    
    
    
}
