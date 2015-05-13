import java.io.File;

abstract class FileConfigurator extends IConfigurator {
    
    protected File configFile; 
    
    protected FileConfigurator(){
	super();
    }
    
    protected FileConfigurator(String s) {
	super();
	configFile = new File(s);
    }
    
    @Override
    protected boolean load(){
	try{
	    super.load();
	    validator();
	}catch(FileFormatException e){
	    errorMessage = e.getMessage();
	    return false;
	}
	return true;
    }
    
    protected void validator() throws FileFormatException{
	if((!configFile.exists() || configFile.isDirectory())){
	    throw new FileFormatException("Ошибка. Файл не существует или является директорией");
	    }
    }
    
    
    
}
