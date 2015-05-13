import java.util.ArrayList;
import java.util.List;

/**
 * ���� ���������� �������� ����� ������ �������������� �������, � ����� ��� ������ ������ ������������'' ̳��''. 
 * ����� � �������� ��� ���� � ���������� � �������� ���������� �� ������ ���������(���� � ����������� ���� 
 * {@link IConfigurator})�������� ��������� �������������� ������� �� ������ ���� ������ �� ������������ �� 
 * ���������� ��������������� �����. ���� ��'������� � ������������� ������� �� ������������ � ����� ������������ 
 * ����� �������������� ������� � ����� �� ����� � ���������� ������� ������������ "�����", � ����� ����� ���� 
 * ����� ���� ���������� ���������. 
 * 
 * @author ���� ����������, ������� ����� ��-21, Բ��, ���� "�ϲ"
 * @version 1.0
 * 
 * @see IConfigurator
 * @see List
 * @see ArrayList
 */
class AbstractConfigurator {
    
    /**
     * ������ �������, �� ������� � ��������� ������������ �� ������ ��������������� �����.
     */
    protected final List<String> errorsList;
    
    /**
     * ���� �������� ��������� �������������� �������, �� ������ ��������� {@link IConfigurator}.
     */
    protected IConfigurator configurator;
    
    /**
     * ����������� ����� ��� ���������. ������� ����������� ���������� �� �������� ���� {@link #errorsList}
     * ����� ����� ��'��� {@link ArrayList};
     * 
     * @see ArrayList
     */
    AbstractConfigurator(){
	super();
	errorsList = new ArrayList<>();
    }
    
    /**
     * ����������� �����. �������� �������� ���� {@link #configurator}.
     * 
     * @param aConfigurator ��'��� ���� {@link IConfigurator}.
     * @param list ��'��� ���� {@link List}.
     */
    AbstractConfigurator(IConfigurator aConfigurator, List<String> list){
	configurator = aConfigurator;
	errorsList = list;
    }
    
    /**
     * ����� ������ ������������ ������������. � ����� ����������� ����� {@link IConfigurator#load()},
     * ��������� ��������� ����� ���������� � �������� ������ �����. ���� ����� �� �������� <code>false</code>,
     * �� � ������ ������� {@link #errorsList} �������� ��������� ������� ������ {@link IConfigurator#getErrorMessage()}.
     * 
     * @return ������ �������� ���������� �������� ������������ ������������. 
     */
    protected boolean loadConfigure(){
	boolean result = configurator.load();
	if(result == false)
	    errorsList.add(configurator.getErrorMessage());
	return result; 

    }
    
    /**
     * ����� ������ ����� ��������������� �����. ����������� ����� {@link IConfigurator#save()},
     * ��������� ��������� ����� ���������� � �������� ������ �����. ���� ����� �� �������� <code>false</code>,
     * �� � ������ ������� {@link #errorsList} �������� ��������� ������� ������ {@link IConfigurator#getErrorMessage()}.
     * 
     * @return ������ �������� ���������� �������� ������ ��������������� �����. 
     */
    protected boolean saveConfigure(){
	boolean result = configurator.save();
	if(result == false)
	    errorsList.add(configurator.getErrorMessage());
	return result; 
    }
    
    /**
     * ����� ������� ������ � ������� �������, ��������� � ��������� ������������/������ ��������������� �����.
     * 
     * @return ������ � ������� �������.
     */
    String getErrors(){
	return errorsList.toString();
    }
}
