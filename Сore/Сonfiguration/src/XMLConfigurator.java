class XMLConfigurator extends FileConfigurator {
    
    protected XMLConfigurator(){
	super();
    }
    
    protected XMLConfigurator(String s) {
	super(s);
    }
    
    @Override
    protected boolean load(){
	return super.load();
    }
    
    @Override
    protected void validator() throws FileFormatException{
	super.validator();
	if(!(configFile.getName().endsWith(".xml")))
	    throw new FileFormatException("Ошибка. Расширение файла не .xml и не .txt");
    }
    
}
