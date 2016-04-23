package Utilidades;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;


/**
 * Extensi�n de JPanel que muestra un reloj digital
 * utilizando javax.swing.Timer, para lo cual implementa ActionListener
 * @author Reynaldo Victor Arceo (Ing. Sistemas Computacionales)
 * @version 1.0
 */
public class Reloj extends JPanel implements ActionListener {
    private SimpleDateFormat sdfFecha=new SimpleDateFormat("EEEE, dd MMMM yyyy");
    private Timer timer;
    private Thread runner;
    private JLabel[] lblNumero;
    private JLabel[] lblSeparador;
    private JLabel lblLogoHora;
    private JLabel lblFecha=new JLabel();
    private JPopupMenu popup;
    private JRadioButtonMenuItem rbLocal;
    private JRadioButtonMenuItem rbUTC;
    private ButtonGroup grpRBHORAS;
    private ImageIcon separador=new ImageIcon(getClass().getResource("/images/separador.gif"));
    private ImageIcon imgLogo_UTC=new ImageIcon(getClass().getResource("/images/logo_hora_utc.gif"));
    private ImageIcon imgLogo_HoraLocal=new ImageIcon(getClass().getResource("/images/logo_hora_local.gif"));
    private ImageIcon[] imgNumeros={
        new ImageIcon(getClass().getResource("/images/0.gif")),
        new ImageIcon(getClass().getResource("/images/1.gif")),
        new ImageIcon(getClass().getResource("/images/2.gif")),
        new ImageIcon(getClass().getResource("/images/3.gif")),
        new ImageIcon(getClass().getResource("/images/4.gif")),
        new ImageIcon(getClass().getResource("/images/5.gif")),
        new ImageIcon(getClass().getResource("/images/6.gif")),
        new ImageIcon(getClass().getResource("/images/7.gif")),
        new ImageIcon(getClass().getResource("/images/8.gif")),
        new ImageIcon(getClass().getResource("/images/9.gif")),
    };
    private int iHoraLocal;
    private int iMinutoLocal;
    private int iSegundoLocal;
    
    private int iDia;
    private int iMes;
    private int iAnno;
    private boolean booUTC;
    private Calendar cal;
    private TimeZone tz;
    
    
    /**
     * Constructor que hace una instancia de la clase tomando la hora del sistema
     * la instancia puede ser para la HORA UTC o la HORA LOCAL
     * @param booUTC - true indica si se inicia el reloj en HORA UTC,
     * false se inicia el reloj en HORA LOCAL
     */
    public Reloj(boolean booUTC){
        this(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),Calendar.getInstance().get(Calendar.SECOND), booUTC);
    }
    /**
     * Constructor para crear una instancia del reloj pasandole la hora, minutos, segundos
     * y si esta hora sera mostrada en tiempo universal o local
     * @param iHoraLocal int, Hora Local
     * @param iMinutoLocal int, Miniutos local
     * @param iSegundoLocal int, Segundos
     * @param booUTC booleano, true para mostrar HORA UTC(HORA UNIVERSAL),
     * false para mostrar HORA LOCAL
     */
    public Reloj(int iHoraLocal,int iMinutoLocal,int iSegundoLocal,boolean booUTC) {
        this.iHoraLocal=iHoraLocal;
        this.iMinutoLocal=iMinutoLocal;
        this.iSegundoLocal=iSegundoLocal;
        this.booUTC=booUTC;
        
        
        cal=Calendar.getInstance();
        tz=cal.getTimeZone();
        iAnno=cal.get(Calendar.YEAR);
        iMes=cal.get(Calendar.MONTH);
        iMes+=1;
        iDia=cal.get(Calendar.DAY_OF_MONTH);
        lblLogoHora=new JLabel();
        if(booUTC)
            lblLogoHora.setIcon(imgLogo_UTC);
        else
            lblLogoHora.setIcon(imgLogo_HoraLocal);
        
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        lblNumero=new JLabel[6];
        lblSeparador=new JLabel[2];
        for(int i=0;i<lblNumero.length;i++)
            lblNumero[i]=new JLabel();
        lblSeparador[0]=new JLabel(separador);
        lblSeparador[1]=new JLabel(separador);
        lblFecha.setForeground(new Color(152,152,200));
        if(this.iHoraLocal>23){
            setHora(0);
            iDia++;
        }
        else
            setHora(this.iHoraLocal);
        setMinuto(this.iMinutoLocal);
        setSegundo(this.iSegundoLocal);
        lblFecha.setText(this.getFechaFormatoLargo());
        CrearMenuPopup();
        this.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e))
                    popup.show(e.getComponent(), e.getX(), e.getY());
            }
            
        });
        
        
        this.add(lblNumero[0]);
        this.add(lblNumero[1]);
        this.add(lblSeparador[0]);
        this.add(lblNumero[2]);
        this.add(lblNumero[3]);
        this.add(lblSeparador[1]);
        this.add(lblNumero[4]);
        this.add(lblNumero[5]);
        
        this.add(lblLogoHora);
        this.add(lblFecha);
        timer=new Timer(1000,this);
        timer.start();

        
    }
    private void CrearMenuPopup(){
        popup=new JPopupMenu("Cambiar Vista Hora");
        rbLocal=new JRadioButtonMenuItem("HORA LOCAL");
        rbLocal.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                cal=Calendar.getInstance();
                iHoraLocal=cal.get(Calendar.HOUR_OF_DAY);
                iDia=cal.get(Calendar.DAY_OF_MONTH);
                lblLogoHora.setIcon(imgLogo_HoraLocal);
                setHora(iHoraLocal);
                lblFecha.setText(getFechaFormatoLargo());
                
            }
            
        });
        rbUTC=new JRadioButtonMenuItem("HORA UTC");
        rbUTC.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                cal=Calendar.getInstance();
                AsignarNuevaHoraUTC();
                lblLogoHora.setIcon(imgLogo_UTC);
                
                if(cal.get(Calendar.HOUR_OF_DAY)+Math.abs(getDiffHorasGMT())>23)
                    iDia++;
                setHora(iHoraLocal);
                lblFecha.setText(getFechaFormatoLargo());
                
            }
            
        });
        if(booUTC)
            rbUTC.setSelected(true);
        else
            rbLocal.setSelected(true);
        
        grpRBHORAS=new ButtonGroup();
        grpRBHORAS.add(rbLocal);
        grpRBHORAS.add(rbUTC);
        popup.add(rbLocal);
        popup.add(rbUTC);
    }
    
    /**
     * Metodo el cual retona la fecha en formato largo (Ej. Miercoles, 6 Abril 2004)
     * @return Retorna un String con la fecha en formato largo
     */
    public String getFechaFormatoLargo(){
        String strFechaTemp="";
        DateFormat df=DateFormat.getDateInstance();
        Calendar calTemp=Calendar.getInstance();
        if(iDia>cal.getActualMaximum(Calendar.DAY_OF_MONTH)){
            iDia=1;
            iMes++;
            if(iMes>12){
                iMes=1;
                iAnno++;
            }
        }
        
        calTemp.set(iAnno,iMes-1,iDia);
        String strFecha=df.format(calTemp.getTime());
        Date date=new Date();
        try{
            df.setLenient(true);
            date=df.parse(strFecha);
        }catch (ParseException parseEx){
            System.out.println(parseEx.getMessage());
        }
        return sdfFecha.format(date);
    }
    private void AsignarNuevaHoraUTC(){
        int iDiffHorasGMT=getDiffHorasGMT();
        if(iDiffHorasGMT<0)
            this.iHoraLocal+=Math.abs(iDiffHorasGMT);
        else
            this.iHoraLocal-=iDiffHorasGMT;
        if(iHoraLocal>23)
            iHoraLocal-=24;
        
        
        
        
    }
    /**
     * Metodo que retorna las horas de diferencia con respecto al GMT, tomando
     * en cuenta el horario de verano
     * @return Retorna un int que representa la direfencia de horas con respecto
     * al GMT
     */
    public int getDiffHorasGMT(){
        int GMTmm=cal.get(Calendar.ZONE_OFFSET);
        int iHora=(GMTmm/1000)/3600;
        if(tz.inDaylightTime(cal.getTime())){
            if(GMTmm<0)
                iHora+=(tz.getDSTSavings()/1000)/3600;
            else
                iHora-=(tz.getDSTSavings()/1000)/3600;
        }
        
        return iHora;
        
    }
    
    /**
     * Metodo para establecer la Hora en formato de 24 hrs
     * @param iHora int, Hora
     */
    public void setHora(int iHora){
        
        int iDecena=iHora/10;
        int iUnidad=iHora%10;
        lblNumero[0].setIcon(imgNumeros[iDecena]);
        lblNumero[1].setIcon(imgNumeros[iUnidad]);
        
    }
    /**
     * Metodo para establecer los minutos
     * @param iMinuto int, Minutos
     */
    public void setMinuto(int iMinuto){
        
        int iDecena=iMinuto/10;
        int iUnidad=iMinuto%10;
        lblNumero[2].setIcon(imgNumeros[iDecena]);
        lblNumero[3].setIcon(imgNumeros[iUnidad]);
    }
    /**
     * Metodo para establecer los segundos
     * @param iSegundo int, Segundos
     */
    public void setSegundo(int iSegundo){
       
        int iDecena=iSegundo/10;
        int iUnidad=iSegundo%10;
        lblNumero[4].setIcon(imgNumeros[iDecena]);
        lblNumero[5].setIcon(imgNumeros[iUnidad]);
        
        
    }
    

    /**
     * Metodo de la interfaz ActionListener
     * @param e ActionEvent, Escuchador de Eventos
     */
    public void actionPerformed(ActionEvent e) {
        this.repaint();
        if(Calendar.getInstance().get(Calendar.SECOND)>0)
            setSegundo(Calendar.getInstance().get(Calendar.SECOND));
        else{
            setSegundo(0);
            
            if(iMinutoLocal<59){
                iMinutoLocal++;
                setMinuto(this.iMinutoLocal);
            }
            else{
                setMinuto(0);
                iMinutoLocal=0;
                if(this.iHoraLocal<23){
                    this.iHoraLocal++;
                    setHora(this.iHoraLocal);
                }
                else{
                    setHora(0);
                    this.iHoraLocal=0;
                    iDia++;
                    lblFecha.setText(this.getFechaFormatoLargo());
                    
                }
            }
            
        }
        
        this.repaint();
    }    
    
}
