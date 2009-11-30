/**
 * Lithuanian (UTF-8) initialisation for the jQuery UI date picker plugin.
 *
 * @author Arturas Paleicikas <arturas@avalon.lt>
 */
jQuery(function($){
	$.datepicker.regional['lt'] = {clearText: 'I????valyti', clearStatus: '',
		closeText: 'U????daryti', closeStatus: '',
		prevText: '&lt;Atgal',  prevStatus: '',
		nextText: 'Pirmyn&gt;', nextStatus: '',
		currentText: '????iandien', currentStatus: '',
		monthNames: ['Sausis','Vasaris','Kovas','Balandis','Gegu????????','Bir????elis',
		'Liepa','Rugpj????tis','Rugs????jis','Spalis','Lapkritis','Gruodis'],
		monthNamesShort: ['Sau','Vas','Kov','Bal','Geg','Bir',
		'Lie','Rugp','Rugs','Spa','Lap','Gru'],
		monthStatus: '', yearStatus: '',
		weekHeader: '', weekStatus: '',
		dayNames: ['sekmadienis','pirmadienis','antradienis','tre????iadienis','ketvirtadienis','penktadienis','????e????tadienis'],
		dayNamesShort: ['sek','pir','ant','tre','ket','pen','????e????'],
		dayNamesMin: ['Se','Pr','An','Tr','Ke','Pe','????e'],
		dayStatus: 'DD', dateStatus: 'D, M d',
		dateFormat: 'yy-mm-dd', firstDay: 1, 
		initStatus: '', isRTL: false};
	$.datepicker.setDefaults($.datepicker.regional['lt']);
});
