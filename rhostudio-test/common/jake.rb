# *****************************************************
# Jake class

class Jake
  def self.config(configfile)
    conf = YAML::load(configfile)
    res = self.config_parse(conf)
    res
  end

  def self.config_parse(conf)
    if conf.is_a?(Array)
      conf.collect! do |x|
        if x.is_a?(Hash) or x.is_a?(Array)
          x = config_parse(x)
          x
        else
          if x =~ /%(.*?)%/
            x.gsub!(/%.*?%/, conf.fetch_r($1).to_s)
          end
          s = x.to_s
          if File.exists? s
            s.gsub!(/\\/, '/')
  	      end
  	      s
        end
      end
    elsif conf.is_a?(Hash)
      newhash = Hash.new

      conf.each do |k,x|
        if x.is_a?(Hash) or x.is_a?(Array)
          newhash[k.to_s] = config_parse(x)
        else
          s = x.to_s
          if File.exists? s
            s.gsub!(/\\/, '/')
          end
          newhash[k.to_s] = s
        end
      end
      conf = newhash

      conf
    end

    conf
  end


  def self.run(command, cd = nil, env = {})
      set_list = []
      env.each_pair do |k, v|
          set_list << "set \"#{k}=#{v}\"&&"
      end

      toPrint = command
      toRun = set_list.join('') + command
      if !cd.nil?
          toPrint = "#{cd}>#{toPrint}"

          if RUBY_PLATFORM =~ /(win|w)32$/
              cd_ = cd.gsub('/', "\\")
              toRun = "cd /d \"#{cd_}\"&&#{toRun}"
          else
              toRun = "cd '#{cd}'&&#{toRun}"
          end
      end

      puts
      puts toPrint
      exitCode = system(toRun)
      exit 1 if !exitCode
      return exitCode
  end
end

# *****************************************************
