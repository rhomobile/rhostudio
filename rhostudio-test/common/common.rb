def getPathByTemplate(startDir, templateName)
  chdir startDir
  items = Dir.glob(templateName)

  if items.length > 1
    puts "Error: Several items '#{glob}' are found:"
    items.each do |item|
       puts "    '#{item}'"
    end
    exit 1
  elsif items.length == 0
    puts 'item not found'
    exit 1
  end

  findPath = File.absolute_path(items[0])
  chdir File.dirname(__FILE__)
  return findPath
end

def getRhostudioSuitePath
  startInstallDrive = "c:\\"      
  suiteNameTemplate = "MotorolaRhoMobileSuite*" 

  findItem = getPathByTemplate(startInstallDrive, suiteNameTemplate)

  return File.absolute_path(findItem)
end

def killProgram(name)
  begin 
    command = 'taskkill /F /IM' + name
    `#{command}`
  rescue Exception => e
    puts e.to_s
  end  
end
