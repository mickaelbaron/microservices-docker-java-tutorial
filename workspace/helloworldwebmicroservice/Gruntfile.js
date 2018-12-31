
module.exports = function(grunt) {
	grunt.loadNpmTasks('grunt-bowercopy');

	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-clean');

	// Default task.
	grunt.registerTask('default', ['bowercopy', 'copy']);

	grunt.registerTask('dependencies', ['bowercopy']);
	grunt.registerTask('package', ['copy']);
	
	var siteDirectory = 'site';
	var libDirectory = siteDirectory + "/lib";
	grunt.initConfig({
		// https://github.com/timmywil/grunt-bowercopy
		bowercopy: {
			jquery: {
				options: {
					destPrefix: libDirectory + '/jquery'
				},
				files: {
					'jquery.min.js': 'jquery/dist/jquery.min.js'
				},
			},
			bootstrap: {
				options: {
					destPrefix: libDirectory
				},
				files: {
					'/bootstrap/css' : 'bootstrap/dist/css/*.min.css',
					'/bootstrap/fonts': 'bootstrap/dist/fonts/*.*',
					'/bootstrap/js': 'bootstrap/dist/js/*.min.js',
				}
			},
			bootstrapvalidator: {
				src: 'bootstrap-validator:main',
				dest: libDirectory + '/bootstrap-validator/'
			}
		},
		copy: {
			main: {
				expand: true,
				cwd: 'src/',
				src: '**',
				dest: 'site/',	
				filter: 'isFile',
			},
		},
		clean: {
			build: ["site", "bower_components"],
			complete: ["site", "bower_components", "node_modules"]
		}
	});  
};
