#!/usr/bin/env ruby

require 'rubygems'
require 'compass'
require 'compass/exec'

runner = Proc.new do
    Dir.entries('frameworks').each do |framework|
        unless "#{framework}" == '.' || "#{framework}" == '..'
            Compass::Frameworks.register_directory("frameworks/#{framework}")
        end
    end
    Compass::Exec::SubCommandUI.new(ARGV).run!
end

exit runner.call || 1
