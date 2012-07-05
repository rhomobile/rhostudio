
def fail
    puts "Error: #{ARGV}"
    exit 1
end

fail if ARGV.length != 2

literal = ARGV[0]
pattern = ARGV[1]

l2 = ''
0.step(pattern.length - 1, 2) do |i|
    l2 += pattern[i, 2].hex.chr
end     

fail if l2 != literal
