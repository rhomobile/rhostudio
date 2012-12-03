class ProcessInfo
  attr_accessor :cmdLine, :pid
   
  def self.parseWmic(cmd)
    result = `#{cmd}`
    raise("Error: " + result) unless $? == 0
    processes = []
    pinfo = nil
    result.split(/\r?\n/).each do |line|
      next if line =~ /^\s*$/
      if line =~ /CommandLine=(.*)/i
        pinfo = ProcessInfo.new
        pinfo.cmdLine = $1
      elsif line =~ /ProcessId=(\d+)/i
        pinfo.pid = $1
        processes << pinfo unless pinfo.pid.to_i == $$.to_i
      end
    end
    return processes
  end
   
  def self.queryProcess(processName)
    return self.parseWmic("wmic process where \"name like '" + processName +
                "%'\" get ProcessID,Commandline /format:list")
  end
   
  def self.queryJavaProcess
    return self.queryProcess('java')
  end 
   
  def to_s
    @pid.to_s + " " + @cmdLine
  end
end 
