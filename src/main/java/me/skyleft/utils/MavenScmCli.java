package me.skyleft.utils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFile;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.ScmRevision;
import org.apache.maven.scm.ScmTag;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.add.AddScmResult;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.manager.BasicScmManager;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.StringUtils;

public class MavenScmCli
{

    private ScmManager scmManager;

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public MavenScmCli()
        throws Exception
    {

        scmManager = new BasicScmManager();

        scmManager.setScmProvider( "svn", new SvnExeScmProvider() );
    }


    public void addFile(String scmUrl, File file){
        List<File> synchronizedFiles = new ArrayList<File>();

        ScmRepository repository;

        try
        {
            repository = scmManager.makeScmRepository( scmUrl );
        }
        catch ( NoSuchScmProviderException ex )
        {
            System.err.println( "Could not find a provider." );

            return ;
        }
        catch ( ScmRepositoryException ex )
        {
            System.err.println( "Error while connecting to the repository" );

            ex.printStackTrace( System.err );

            return ;
        }
        ScmFileSet scmFileSet = null;
        File dir = file.getParentFile();
        File fi = new File(file.getName());
        ScmFileSet fileSet = new ScmFileSet(dir, fi);
        try {
            scmFileSet = new ScmFileSet(file.getParentFile(),file);
            AddScmResult addScmResult = scmManager.add(repository, scmFileSet);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void execute( String scmUrl, String command, File workingDirectory)
    {
        ScmRepository repository;

        try
        {
            repository = scmManager.makeScmRepository( scmUrl );
        }
        catch ( NoSuchScmProviderException ex )
        {
            System.err.println( "Could not find a provider." );

            return;
        }
        catch ( ScmRepositoryException ex )
        {
            System.err.println( "Error while connecting to the repository" );

            ex.printStackTrace( System.err );

            return;
        }

        try
        {
            if ( command.equals( "checkout" ) )
            {
                checkOut( repository, workingDirectory );
            }
            else if ( command.equals( "checkin" ) )
            {
                checkIn( repository, workingDirectory );
            }
            else if ( command.equals( "update" ) )
            {
                update( repository, workingDirectory );
            }
            else
            {
                System.err.println( "Unknown SCM command '" + command + "'." );
            }
        }
        catch ( ScmException ex )
        {
            System.err.println( "Error while executing the SCM command." );

            ex.printStackTrace( System.err );

            return;
        }
    }


    public void execute( String scmUrl, String command, File workingDirectory,boolean rec)
    {
        ScmRepository repository;

        try
        {
            repository = scmManager.makeScmRepository( scmUrl );
        }
        catch ( NoSuchScmProviderException ex )
        {
            System.err.println( "Could not find a provider." );

            return;
        }
        catch ( ScmRepositoryException ex )
        {
            System.err.println( "Error while connecting to the repository" );

            ex.printStackTrace( System.err );

            return;
        }

        try
        {
            if ( command.equals( "checkout" ) )
            {
                checkOut( repository, workingDirectory ,rec);
            }
            else if ( command.equals( "checkin" ) )
            {
                checkIn( repository, workingDirectory);
            }
            else if ( command.equals( "update" ) )
            {
                update( repository, workingDirectory);
            }
            else
            {
                System.err.println( "Unknown SCM command '" + command + "'." );
            }
        }
        catch ( ScmException ex )
        {
            System.err.println( "Error while executing the SCM command." );

            ex.printStackTrace( System.err );

            return;
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void checkOut( ScmRepository scmRepository, File workingDirectory)
        throws ScmException
    {
        if ( workingDirectory.exists() )
        {
            System.err.println( "The working directory already exist: '" + workingDirectory.getAbsolutePath()
                + "'." );

            return;
        }

        if ( !workingDirectory.mkdirs() )
        {
            System.err.println(
                "Error while making the working directory: '" + workingDirectory.getAbsolutePath() + "'." );

            return;
        }

        CheckOutScmResult result = scmManager.checkOut( scmRepository, new ScmFileSet( workingDirectory ) );

        if ( !result.isSuccess() )
        {
            showError( result );

            return;
        }

        List<ScmFile> checkedOutFiles = result.getCheckedOutFiles();

        System.out.println( "Checked out these files: " );

        for ( ScmFile file : checkedOutFiles )
        {
            System.out.println( " " + file.getPath() );
        }
    }

    private void checkOut( ScmRepository scmRepository, File workingDirectory,boolean rec)
            throws ScmException
    {
        if ( workingDirectory.exists() )
        {
            System.err.println( "The working directory already exist: '" + workingDirectory.getAbsolutePath()
                    + "'." );

            return;
        }

        if ( !workingDirectory.mkdirs() )
        {
            System.err.println(
                    "Error while making the working directory: '" + workingDirectory.getAbsolutePath() + "'." );

            return;
        }

        CheckOutScmResult result = scmManager.checkOut( scmRepository, new ScmFileSet( workingDirectory ),rec);

        if ( !result.isSuccess() )
        {
            showError( result );

            return;
        }

        List<ScmFile> checkedOutFiles = result.getCheckedOutFiles();

        System.out.println( "Checked out these files: " );

        for ( ScmFile file : checkedOutFiles )
        {
            System.out.println( " " + file.getPath() );
        }
    }

    private void checkIn( ScmRepository scmRepository, File workingDirectory )
        throws ScmException
    {
        if ( !workingDirectory.exists() )
        {
            System.err.println( "The working directory doesn't exist: '" + workingDirectory.getAbsolutePath()
                + "'." );

            return;
        }

        String message = "";

        CheckInScmResult result =
            scmManager.checkIn( scmRepository, new ScmFileSet( workingDirectory ), message );

        if ( !result.isSuccess() )
        {
            showError( result );

            return;
        }

        List<ScmFile> checkedInFiles = result.getCheckedInFiles();

        System.out.println( "Checked in these files: " );

        for ( ScmFile file : checkedInFiles )
        {
            System.out.println( " " + file.getPath() );
        }
    }

    private void update( ScmRepository scmRepository, File workingDirectory)
        throws ScmException
    {
        if ( !workingDirectory.exists() )
        {
            System.err.println( "The working directory doesn't exist: '" + workingDirectory.getAbsolutePath()
                + "'." );

            return;
        }

        UpdateScmResult result = scmManager.update( scmRepository, new ScmFileSet( workingDirectory ));

        if ( !result.isSuccess() )
        {
            showError( result );

            return;
        }

        List<ScmFile> updatedFiles = result.getUpdatedFiles();

        System.out.println( "Updated these files: " );

        for ( ScmFile file : updatedFiles )
        {
            System.out.println( " " + file.getPath() );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void showError( ScmResult result )
    {
        System.err.println( "There was a error while executing the SCM command." );

        String providerMessage = result.getProviderMessage();

        if ( !StringUtils.isEmpty( providerMessage ) )
        {
            System.err.println( "Error message from the provider: " + providerMessage );
        }
        else
        {
            System.err.println( "The provider didn't give a error message." );
        }

        String output = result.getCommandOutput();

        if ( !StringUtils.isEmpty( output ) )
        {
            System.err.println( "Command output:" );

            System.err.println( output );
        }
    }

}