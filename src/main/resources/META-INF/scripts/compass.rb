#!/usr/bin/env ruby

require 'rubygems'
require 'compass'
require 'compass/exec'

runner = Proc.new do

# command-injection
    Compass::Exec::SubCommandUI.new(ARGV).run!
end

exit runner.call || 1
