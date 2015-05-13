import java.util.ArrayList;
import java.util.List;

/**
 * Клас інкапсулює загальну логіку роботи конфігураційної системи, в основі якої лежить шаблон проектування'' Міст''. 
 * Згідно з шаблоном цей клас є початковим в ієрархії абстракцій та агрегує інтерфейс(яким є абстрактний клас 
 * {@link IConfigurator})ієрархії реалізацій конфігураційної системи та делегує йому запити на завантаження чи 
 * збереження конфігураційних даних. Клас об'явлений з модифікатором доступу за умовчуванням з метою інкапсуляції 
 * логіки конфігурування системи у пакеті та згідно з реалізацією шаблона проектування "Фасад", у якому даний клас 
 * відіграє роль компонента підсистеми. 
 * 
 * @author Олег Кахерський, студент групи ІО-21, ФІОТ, НТУУ "КПІ"
 * @version 1.0
 * 
 * @see IConfigurator
 * @see List
 * @see ArrayList
 */
class AbstractConfigurator {
    
    /**
     * Список помилок, що виникли в результаті завантаження чи запису конфігураційних даних.
     */
    protected final List<String> errorsList;
    
    /**
     * Клас ієрархії реалізацій конфігураційної системи, що реалізує інтерфейс {@link IConfigurator}.
     */
    protected IConfigurator configurator;
    
    /**
     * Конструктор класу без параметрів. Викликає конструктор суперкласу та ініціалізує поле {@link #errorsList}
     * через новий об'єкт {@link ArrayList};
     * 
     * @see ArrayList
     */
    AbstractConfigurator(){
	super();
	errorsList = new ArrayList<>();
    }
    
    /**
     * Конструктор класу. Ініціалізує значення поля {@link #configurator}.
     * 
     * @param aConfigurator об'єкт типу {@link IConfigurator}.
     * @param list об'єкт типу {@link List}.
     */
    AbstractConfigurator(IConfigurator aConfigurator, List<String> list){
	configurator = aConfigurator;
	errorsList = list;
    }
    
    /**
     * Метод виконує завантаження конфігурацій. У методі викликається метод {@link IConfigurator#load()},
     * результат виконання якого записується в локальну булеву змінну. Якщо змінна має значення <code>false</code>,
     * то в список помилок {@link #errorsList} додається результат виклику методу {@link IConfigurator#getErrorMessage()}.
     * 
     * @return булеве значення результату операції завантаження конфігурацій. 
     */
    protected boolean loadConfigure(){
	boolean result = configurator.load();
	if(result == false)
	    errorsList.add(configurator.getErrorMessage());
	return result; 

    }
    
    /**
     * Метод виконує запис конфігураційних даних. Викликаєтсья метод {@link IConfigurator#save()},
     * результат виконання якого записується в локальну булеву змінну. Якщо змінна має значення <code>false</code>,
     * то в список помилок {@link #errorsList} додається результат виклику методу {@link IConfigurator#getErrorMessage()}.
     * 
     * @return булеве значення результату операції запису конфігураційних даних. 
     */
    protected boolean saveConfigure(){
	boolean result = configurator.save();
	if(result == false)
	    errorsList.add(configurator.getErrorMessage());
	return result; 
    }
    
    /**
     * Метод повертає строку зі списком помилок, виконаних в результаті завантаження/запису конфігураційних даних.
     * 
     * @return строка зі списком помилок.
     */
    String getErrors(){
	return errorsList.toString();
    }
}
